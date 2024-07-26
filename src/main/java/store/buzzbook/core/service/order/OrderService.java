package store.buzzbook.core.service.order;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;
import static store.buzzbook.core.common.listener.WrappingListener.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.order.AddressNotFoundException;
import store.buzzbook.core.common.exception.order.AlreadyCanceledException;
import store.buzzbook.core.common.exception.order.AlreadyRefundedException;
import store.buzzbook.core.common.exception.order.AlreadyShippingOutException;
import store.buzzbook.core.common.exception.order.DeliveryPolicyNotFoundException;
import store.buzzbook.core.common.exception.order.ExpiredToRefundException;
import store.buzzbook.core.common.exception.order.NotPaidException;
import store.buzzbook.core.common.exception.order.NotShippedException;
import store.buzzbook.core.common.exception.order.OrderDetailNotFoundException;
import store.buzzbook.core.common.exception.order.OrderStatusNotFoundException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.WrappingNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreatePointLogForOrderRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadOrdersResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.dto.point.PointLogResponse;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.mapper.order.DeliveryPolicyMapper;
import store.buzzbook.core.mapper.order.OrderDetailMapper;
import store.buzzbook.core.mapper.order.OrderMapper;
import store.buzzbook.core.mapper.order.OrderStatusMapper;
import store.buzzbook.core.mapper.order.WrappingMapper;
import store.buzzbook.core.repository.order.DeliveryPolicyRepository;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.order.WrappingRepository;
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.AddressRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.point.PointService;
import store.buzzbook.core.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
	private static final int REFUND_PERIOD = 10;
	private static final int BREAKAGE_REFUND_PERIOD = 30;

	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UserRepository userRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final WrappingRepository wrappingRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final UserService userService;
	private final AddressRepository addressRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final PointService pointService;

	@PersistenceContext
	private EntityManager entityManager;

	@Cacheable(value = "readOrders", key = "#request.page")
	@Transactional(readOnly = true)
	public Map<String, Object> readOrders(ReadOrdersRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Slice<ReadOrdersResponse> responses = orderRepository.findAll(request, pageable);

		data.put("responseData", responses.getContent());
		data.put("hasNext", responses.hasNext());

		return data;
	}

	@Transactional(readOnly = true)
	public Map<String, Object> readMyOrders(ReadOrdersRequest request, String loginId) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Slice<ReadOrdersResponse> responses = orderRepository.findAllByUser_LoginId(request, loginId, pageable);

		data.put("responseData", responses.getContent());
		data.put("hasNext", responses.hasNext());

		return data;
	}

	@CacheEvict(value = "readOrders", allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public ReadOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		List<CreateOrderDetailRequest> details = createOrderRequest.getDetails();
		User user = null;
		if (createOrderRequest.getLoginId() != null && !createOrderRequest.getLoginId().isBlank()) {
			UserInfo userInfo = userService.getUserInfoByLoginId(createOrderRequest.getLoginId());

			user = userRepository.findById(userInfo.id())
				.orElseThrow(() -> new UserNotFoundException(userInfo.loginId()));
		}

		Order order = null;

		if (createOrderRequest.getAddress().isEmpty()) {
			Optional<Address> address = addressRepository.findById(Long.parseLong(createOrderRequest.getAddresses()));
			if (address.isPresent()) {
				order = orderRepository.save(OrderMapper.toEntityWithAddress(createOrderRequest, user, address.get()));
			} else {
				throw new AddressNotFoundException();
			}

		} else {
			order = orderRepository.save(OrderMapper.toEntity(createOrderRequest, user));
		}

		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (CreateOrderDetailRequest detail : details) {
			detail.setOrderId(order.getId());
			OrderStatus orderStatus = orderStatusRepository.findById(detail.getOrderStatusId())
				.orElseThrow(OrderStatusNotFoundException::new);
			Wrapping wrapping = null;
			if (detail.getWrappingId() != 0) {
				wrapping = wrappingRepository.findById(detail.getWrappingId())
					.orElseThrow(WrappingNotFoundException::new);
			} else {
				wrapping = wrappingRepository.findByPaper(UNPACKAGED);
			}

			Product product = productRepository.findById(detail.getProductId())
				.orElseThrow(ProductNotFoundException::new);

			product.decreaseStock(detail.getQuantity());

			detail.setPrice(product.getPrice());

			OrderDetail orderDetail = OrderDetailMapper.toEntity(detail, order, wrapping, product, orderStatus);
			orderDetail = orderDetailRepository.save(orderDetail);

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			ReadWrappingResponse readWrappingResponse = null;
			if (wrapping != null) {
				readWrappingResponse = WrappingMapper.toDto(wrapping);
			}

			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}
		if (user == null) {
			return OrderMapper.toDto(order, readOrderDetailResponse, null);
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, user.getLoginId());
	}

	@Transactional(rollbackFor = Exception.class)
	public PointLogResponse updatePointLog(CreatePointLogForOrderRequest createPointLogForOrderRequest, UserInfo userInfo) {
		User user = userRepository.findByLoginId(userInfo.loginId()).orElseThrow(() -> new UserNotFoundException(userInfo.loginId()));
		double pointRate = pointPolicyRepository.findByName(createPointLogForOrderRequest.getPointPolicyName()).getRate();
		int benefit = (int)(createPointLogForOrderRequest.getPrice() * userInfo.grade().benefit());
		int point = (int)(createPointLogForOrderRequest.getPrice() * pointRate);
		PointLog pointLog = pointService.createPointLogWithDelta(user, createPointLogForOrderRequest.getPointOrderInquiry(), point+benefit);

		return PointLogResponse.from(pointLog);
	}

	@CacheEvict(value = "readOrders", allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public ReadOrderResponse updateOrderWithAdmin(UpdateOrderRequest updateOrderRequest) {
		Order order = orderRepository.findByOrderStr(updateOrderRequest.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());

		if (updateOrderRequest.getOrderStatusName().equals(SHIPPING_OUT)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPING_OUT))) {
					throw new AlreadyShippingOutException();
				}
			}
		}

		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();
		if (updateOrderRequest.getOrderStatusName().equals(REFUND)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(CANCELED))) {
					throw new AlreadyCanceledException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(REFUND)) || orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
					throw new AlreadyRefundedException();
				}
				if (!orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED))) {
					throw new NotShippedException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED)) && isCreatedBeforeDays(orderDetail.getCreateAt(), REFUND_PERIOD)) {
					throw new ExpiredToRefundException();
				}
			}
		}

		if (updateOrderRequest.getOrderStatusName().equals(BREAKAGE_REFUND)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(CANCELED))) {
					throw new AlreadyCanceledException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(REFUND)) || orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
					throw new AlreadyRefundedException();
				}
				if (!orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED))) {
					throw new NotShippedException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED)) && isCreatedBeforeDays(orderDetail.getCreateAt(), BREAKAGE_REFUND_PERIOD)) {
					throw new ExpiredToRefundException();
				}
			}
		}

		if (updateOrderRequest.getOrderStatusName().equals(CANCELED)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(REFUND)) || orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
					throw new AlreadyRefundedException();
				}
				if (!orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(PAID))) {
					throw new NotPaidException();
				}
			}
		}

		OrderStatus orderStatus = orderStatusRepository.findByName(updateOrderRequest.getOrderStatusName());

		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.changeOrderStatus(orderStatus);
			entityManager.flush();

			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(ProductNotFoundException::new);

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
				.orElseThrow(WrappingNotFoundException::new);

			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);
			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, order.getUser().getLoginId());
	}

	@CacheEvict(value = "getOrders", allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public ReadOrderResponse updateOrder(UpdateOrderRequest updateOrderRequest, String loginId) {
		Order order = orderRepository.findByOrderStr(updateOrderRequest.getOrderId());

		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(
			order.getId(), loginId);

		if (updateOrderRequest.getOrderStatusName().equals(REFUND)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(CANCELED))) {
					throw new AlreadyCanceledException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(REFUND))
					|| orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
					throw new AlreadyRefundedException();
				}
				if (!orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED))) {
					throw new NotShippedException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED))
					&& isCreatedBeforeDays(orderDetail.getCreateAt(), REFUND_PERIOD)) {
					throw new ExpiredToRefundException();
				}
			}
		}

		if (updateOrderRequest.getOrderStatusName().equals(BREAKAGE_REFUND)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(CANCELED))) {
					throw new AlreadyCanceledException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(REFUND))
					|| orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
					throw new AlreadyRefundedException();
				}
				if (!orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED))) {
					throw new NotShippedException();
				}
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(SHIPPED))
					&& isCreatedBeforeDays(orderDetail.getCreateAt(), BREAKAGE_REFUND_PERIOD)) {
					throw new ExpiredToRefundException();
				}
			}
		}

		if (updateOrderRequest.getOrderStatusName().equals(CANCELED)) {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(REFUND)) || orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(BREAKAGE_REFUND))) {
					throw new AlreadyRefundedException();
				}
				if (!orderDetail.getOrderStatus().equals(orderStatusRepository.findByName(PAID))) {
					throw new NotPaidException();
				}
			}
		}

		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();
		OrderStatus orderStatus = orderStatusRepository.findByName(updateOrderRequest.getOrderStatusName());

		for (OrderDetail orderDetail : orderDetails) {
			orderDetail.changeOrderStatus(orderStatus);
			entityManager.flush();

			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(ProductNotFoundException::new);

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
				.orElseThrow(WrappingNotFoundException::new);

			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);
			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, order.getUser().getLoginId());
	}

	private static boolean isCreatedBeforeDays(LocalDateTime createAt, int sub) {
		LocalDateTime daysAgo = LocalDateTime.now().minus(sub, ChronoUnit.DAYS);
		return createAt.isBefore(daysAgo);
	}

	@Transactional(readOnly = true)
	public ReadOrderResponse readOrder(ReadOrderRequest request, String loginId) {
		Order order = orderRepository.findByOrderStr(request.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());

		return OrderMapper.toDto(order, convertOrderDetailsToDto(orderDetails), loginId);
	}

	private List<ReadOrderDetailResponse> convertOrderDetailsToDto(List<OrderDetail> orderDetails) {
		List<ReadOrderDetailResponse> details = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(ProductNotFoundException::new);

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
				.orElseThrow(WrappingNotFoundException::new);
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			details.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return details;
	}

	@Transactional(readOnly = true)
	public ReadOrderResponse readOrderWithoutLogin(ReadOrderWithoutLoginRequest request) {
		Order order = orderRepository.findByOrderStr(request.getOrderId());
		if (order.getUser() != null) {
			throw new NotAuthorizedException("비회원 주문만 조회 가능합니다.");
		}

		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_OrderEmail(order.getId(),
			request.getOrderEmail());

		return OrderMapper.toDto(order, convertOrderDetailsToDto(orderDetails), null);
	}

	@Transactional(readOnly = true)
	public ReadOrderStatusResponse readOrderStatusById(int id) {
		return OrderStatusMapper.toDto(orderStatusRepository.findById(id)
			.orElseThrow(OrderStatusNotFoundException::new));
	}

	@Transactional(readOnly = true)
	public ReadOrderStatusResponse readOrderStatusByName(String orderStatusName) {
		return OrderStatusMapper.toDto(orderStatusRepository.findByName(orderStatusName));
	}

	@Transactional(readOnly = true)
	public List<ReadOrderStatusResponse> readAllOrderStatus() {
		return orderStatusRepository.findAll().stream().map(OrderStatusMapper::toDto).toList();
	}

	@Transactional
	public ReadDeliveryPolicyResponse createDeliveryPolicy(CreateDeliveryPolicyRequest createDeliveryPolicyRequest) {
		return DeliveryPolicyMapper.toDto(
			deliveryPolicyRepository.save(DeliveryPolicy.builder()
				.name(createDeliveryPolicyRequest.getName())
				.policyPrice(createDeliveryPolicyRequest.getPolicyPrice())
				.standardPrice(createDeliveryPolicyRequest.getStandardPrice())
				.deleted(false)
				.build()));
	}

	@Transactional
	public void deleteDeliveryPolicy(int deliveryPolicyId) {
		DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(deliveryPolicyId).orElseThrow(
			DeliveryPolicyNotFoundException::new);
		deliveryPolicy.delete();
	}

	@Transactional(readOnly = true)
	public ReadDeliveryPolicyResponse readDeliveryPolicyById(int deliveryPolicyId) {
		return DeliveryPolicyMapper.toDto(deliveryPolicyRepository.findById(deliveryPolicyId)
			.orElseThrow(DeliveryPolicyNotFoundException::new));
	}

	@Transactional(readOnly = true)
	public List<ReadDeliveryPolicyResponse> readAllDeliveryPolicy() {
		return deliveryPolicyRepository.findAll().stream().filter(deliveryPolicy -> !deliveryPolicy.isDeleted()).map(DeliveryPolicyMapper::toDto).toList();
	}

	@Transactional
	public ReadWrappingResponse createWrapping(CreateWrappingRequest createWrappingRequest) {
		return WrappingMapper.toDto(wrappingRepository.save(Wrapping.builder().paper(createWrappingRequest.getPaper())
			.price(createWrappingRequest.getPrice()).deleted(false).build()));
	}

	@Transactional
	public void deleteWrapping(int wrappingId) {
		Wrapping wrapping = wrappingRepository.findById(wrappingId).orElseThrow(WrappingNotFoundException::new);
		wrapping.delete();
	}

	@Transactional(readOnly = true)
	public ReadWrappingResponse readWrappingById(int wrappingId) {
		return WrappingMapper.toDto(wrappingRepository.findById(wrappingId)
			.orElseThrow(WrappingNotFoundException::new));
	}

	@Transactional(readOnly = true)
	public List<ReadWrappingResponse> readAllWrapping() {
		return wrappingRepository.findAll().stream().filter(wrapping -> !wrapping.isDeleted()).map(WrappingMapper::toDto).toList();
	}

	@Transactional(rollbackFor = Exception.class)
	public ReadOrderDetailResponse updateOrderDetail(UpdateOrderDetailRequest request, String loginId) {
		OrderDetail orderDetail = orderDetailRepository.findByIdAndOrder_User_LoginId(request.getId(), loginId);
		orderDetailRepository.save(OrderDetail.builder()
			.orderStatus(orderStatusRepository.findByName(request.getOrderStatusName()))
			.id(orderDetail.getId())
			.wrap(orderDetail.isWrap())
			.createAt(orderDetail.getCreateAt())
			.price(orderDetail.getPrice())
			.quantity(orderDetail.getQuantity())
			.order(orderDetail.getOrder())
			.wrapping(orderDetail.getWrapping())
			.product(orderDetail.getProduct())
			.updateAt(LocalDateTime.now())
			.build());

		Product product = productRepository.findById(orderDetail.getProduct().getId())
			.orElseThrow(ProductNotFoundException::new);

		if (request.getOrderStatusName().equals(CANCELED) || request.getOrderStatusName().equals(PARTIAL_CANCELED)
			|| request.getOrderStatusName().equals(REFUND) || request.getOrderStatusName().equals(PARTIAL_REFUND)) {
			product.increaseStock(orderDetail.getQuantity());
		}

		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
			.orElseThrow(WrappingNotFoundException::new);
		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

		ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

		return OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse);
	}

	@Transactional(rollbackFor = Exception.class)
	public ReadOrderDetailResponse updateOrderDetailWithAdmin(UpdateOrderDetailRequest request) {
		OrderDetail orderDetail = orderDetailRepository.findById(request.getId())
			.orElseThrow(OrderDetailNotFoundException::new);
		orderDetailRepository.save(OrderDetail.builder()
			.orderStatus(orderStatusRepository.findByName(request.getOrderStatusName()))
			.id(orderDetail.getId())
			.wrap(orderDetail.isWrap())
			.createAt(orderDetail.getCreateAt())
			.price(orderDetail.getPrice())
			.quantity(orderDetail.getQuantity())
			.order(orderDetail.getOrder())
			.wrapping(orderDetail.getWrapping())
			.product(orderDetail.getProduct())
			.updateAt(LocalDateTime.now())
			.build());

		Product product = productRepository.findById(orderDetail.getProduct().getId())
			.orElseThrow(ProductNotFoundException::new);

		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId())
			.orElseThrow(WrappingNotFoundException::new);
		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

		ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

		return OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse);
	}

	@Transactional(readOnly = true)
	public String readOrderStr(long orderDetailId) {
		return orderDetailRepository.findOrderStrByOrderDetailId(orderDetailId);
	}
}
