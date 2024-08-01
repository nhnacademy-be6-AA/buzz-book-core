package store.buzzbook.core.service.order;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;
import static store.buzzbook.core.common.listener.WrappingListener.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
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

/**
 * 주문 관련 서비스
 * 주문 조회, 생성, 수정 기능과 주문 상태, 포장, 운임비 정책 조회, 생성, 삭제 기능을 제공합니다.
 *
 * @author 박설
 */

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

	/**
	 * 모든 주문 내역을 조회합니다.
	 *
	 * @param request 주문 조회 요청 객체
	 * @return 주문 내역 리스트와 다음 페이지 유무 여부를 가진 Map 객체
	 */

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

	/**
	 * 내 주문 내역을 조회합니다.
	 *
	 * @param request 주문 조회 요청 객체와 로그인 아이디
	 * @return 내 주문 내역 리스트와 다음 페이지 유무 여부를 가진 Map 객체
	 */

	@Transactional(readOnly = true)
	public Map<String, Object> readMyOrders(ReadOrdersRequest request, String loginId) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Slice<ReadOrdersResponse> responses = orderRepository.findAllByUser_LoginId(request, loginId, pageable);

		data.put("responseData", responses.getContent());
		data.put("hasNext", responses.hasNext());

		return data;
	}

	/**
	 * 주문을 생성합니다.
	 *
	 * @param createOrderRequest 주문 생성 요청 객체
	 * @return 생성된 주문 반환
	 */

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
		OrderStatus orderStatus = orderStatusRepository.findByName(createOrderRequest.getOrderStatus());

		if (createOrderRequest.getAddress().isEmpty()) {
			Optional<Address> address = addressRepository.findById(Long.parseLong(createOrderRequest.getAddresses()));
			if (address.isPresent()) {
				order = orderRepository.save(OrderMapper.toEntityWithAddress(createOrderRequest, user, orderStatus, address.get()));
			} else {
				throw new AddressNotFoundException();
			}

		} else {
			order = orderRepository.save(OrderMapper.toEntity(createOrderRequest, orderStatus, user));
		}

		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (CreateOrderDetailRequest detail : details) {
			detail.setOrderId(order.getId());
			OrderStatus orderDetailStatus = orderStatusRepository.findByName(detail.getOrderStatus());
			Wrapping wrapping = null;
			if (detail.getWrappingId() != 0) {
				wrapping = wrappingRepository.findById(detail.getWrappingId())
					.orElseThrow(WrappingNotFoundException::new);
			} else {
				wrapping = wrappingRepository.findByPaper(UNPACKAGED);
			}

			Product product = productRepository.findById(detail.getProductId())
				.orElseThrow(ProductNotFoundException::new);

			detail.setPrice(product.getPrice());

			OrderDetail orderDetail = OrderDetailMapper.toEntity(detail, order, wrapping, product, orderDetailStatus);
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

	/**
	 * 포인트 내역을 생성합니다.
	 *
	 * @param createPointLogForOrderRequest 포인트 내역 생성 요청 객체
	 * @param userInfo 유저 정보 객체
	 * @return 생성된 포인트 내역 반환
	 */

	// @Transactional(rollbackFor = Exception.class)
	// public PointLogResponse createPointLog(CreatePointLogForOrderRequest createPointLogForOrderRequest, UserInfo userInfo) {
	// 	User user = userRepository.findByLoginId(userInfo.loginId()).orElseThrow(() -> new UserNotFoundException(userInfo.loginId()));
	// 	double pointRate = pointPolicyRepository.findByName(createPointLogForOrderRequest.getPointPolicyName()).getRate();
	// 	int benefit = (int)(createPointLogForOrderRequest.getPrice() * userInfo.grade().benefit());
	// 	int point = (int)(createPointLogForOrderRequest.getPrice() * pointRate);
	// 	PointLog pointLog = pointService.createPointLogWithDelta(user, createPointLogForOrderRequest.getPointOrderInquiry(), point+benefit);
	//
	// 	return PointLogResponse.from(pointLog);
	// }

	/**
	 * 관리자가 주문을 수정하는 기능입니다.
	 *
	 * @param updateOrderRequest 주문 업데이트 요청 객체
	 * @return 업데이트된 주문 응답 객체
	 */

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

	/**
	 * 고객이 주문을 수정하는 기능입니다.
	 *
	 * @param updateOrderRequest 주문 업데이트 요청 객체
	 * @param loginId 고객 아이디
	 * @return 업데이트된 주문 응답 객체
	 */

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

	/**
	 * 회원이 주문을 조회합니다.
	 *
	 * @param request 주문 조회 요청 객체
	 * @param loginId 고객 아이디
	 * @return 주문 응답 객체
	 */

	@Transactional(readOnly = true)
	public ReadOrderResponse readOrder(ReadOrderRequest request, String loginId) {
		Order order = orderRepository.findByOrderStr(request.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());

		return OrderMapper.toDto(order, convertOrderDetailsToDto(orderDetails), loginId);
	}

	/**
	 * 주문 상세 엔티티 리스트를 DTO 리스트로 변환합니다.
	 *
	 * @param orderDetails 주문 상세 엔티티 리스트
	 * @return 주문 상세 DTO 응답 리스트
	 */

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

	/**
	 * 비회원이 주문을 조회합니다.
	 *
	 * @param request 비회원 주문 조회 요청 객체
	 * @return 주문 응답 객체
	 */

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

	/**
	 * 번호로 주문 상태를 조회합니다.
	 *
	 * @param id 주문 상태 번호
	 * @return 주문 상태 응답 객체
	 */

	@Transactional(readOnly = true)
	public ReadOrderStatusResponse readOrderStatusById(int id) {
		return OrderStatusMapper.toDto(orderStatusRepository.findById(id)
			.orElseThrow(OrderStatusNotFoundException::new));
	}

	/**
	 * 이름으로 주문 상태를 조회합니다.
	 *
	 * @param orderStatusName 주문 상태 이름
	 * @return 주문 상태 응답 객체
	 */

	@Transactional(readOnly = true)
	public ReadOrderStatusResponse readOrderStatusByName(String orderStatusName) {
		return OrderStatusMapper.toDto(orderStatusRepository.findByName(orderStatusName));
	}

	/**
	 * 모든 주문 상태들을 조회합니다.
	 *
	 * @return 주문 상태 응답 객체 리스트
	 */

	@Transactional(readOnly = true)
	public List<ReadOrderStatusResponse> readAllOrderStatus() {
		return orderStatusRepository.findAll().stream().map(OrderStatusMapper::toDto).toList();
	}

	/**
	 * 운임비 정책을 생성합니다.
	 *
	 * @param createDeliveryPolicyRequest 운임비 정책 생성 요청 객체
	 * @return 운임비 정책 응답 객체
	 */

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

	/**
	 * 운임비 정책을 삭제합니다.
	 *
	 * @param deliveryPolicyId 운임비 정책 번호
	 */

	@Transactional
	public void deleteDeliveryPolicy(int deliveryPolicyId) {
		DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(deliveryPolicyId).orElseThrow(
			DeliveryPolicyNotFoundException::new);
		deliveryPolicy.delete();
	}

	/**
	 * 번호로 운임비 정책을 조회합니다.
	 *
	 * @param deliveryPolicyId 운임비 정책 번호
	 * @return 운임비 정책 응답 객체
	 */

	@Transactional(readOnly = true)
	public ReadDeliveryPolicyResponse readDeliveryPolicyById(int deliveryPolicyId) {
		return DeliveryPolicyMapper.toDto(deliveryPolicyRepository.findById(deliveryPolicyId)
			.orElseThrow(DeliveryPolicyNotFoundException::new));
	}

	/**
	 * 모든 운임비 정책들을 조회합니다.
	 *
	 * @return 운임비 정책 응답 객체 리스트
	 */

	@Transactional(readOnly = true)
	public List<ReadDeliveryPolicyResponse> readAllDeliveryPolicy() {
		return deliveryPolicyRepository.findAll().stream().filter(deliveryPolicy -> !deliveryPolicy.isDeleted()).map(DeliveryPolicyMapper::toDto).toList();
	}

	/**
	 * 포장지를 생성합니다.
	 *
	 * @param createWrappingRequest 포장지 생성 요청 객체
	 * @return 포장지 응답 객체
	 */

	@Transactional
	public ReadWrappingResponse createWrapping(CreateWrappingRequest createWrappingRequest) {
		return WrappingMapper.toDto(wrappingRepository.save(Wrapping.builder().paper(createWrappingRequest.getPaper())
			.price(createWrappingRequest.getPrice()).deleted(false).build()));
	}

	/**
	 * 포장지를 삭제합니다.
	 *
	 * @param wrappingId 포장지 번호
	 */

	@Transactional
	public void deleteWrapping(int wrappingId) {
		Wrapping wrapping = wrappingRepository.findById(wrappingId).orElseThrow(WrappingNotFoundException::new);
		wrapping.delete();
	}

	/**
	 * 번호로 포장지를 조회합니다.
	 *
	 * @param wrappingId 포장지 번호
	 * @return 포장지 응답 객체
	 */

	@Transactional(readOnly = true)
	public ReadWrappingResponse readWrappingById(int wrappingId) {
		return WrappingMapper.toDto(wrappingRepository.findById(wrappingId)
			.orElseThrow(WrappingNotFoundException::new));
	}

	/**
	 * 모든 포장지를 조회합니다.
	 *
	 * @return 포장지 응답 객체 리스트
	 */

	@Transactional(readOnly = true)
	public List<ReadWrappingResponse> readAllWrapping() {
		return wrappingRepository.findAll().stream().filter(wrapping -> !wrapping.isDeleted()).map(WrappingMapper::toDto).toList();
	}

	/**
	 * 고객이 주문 상세를 수정합니다.
	 *
	 * @param request 주문 상세 업데이트 요청 객체
	 * @param loginId 고객 아이디
	 * @return 주문 상세 응답 객체
	 */

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

	/**
	 * 관리자가 주문 상세를 수정합니다.
	 *
	 * @param request 주문 상세 업데이트 요청 객체
	 * @return 주문 상세 응답 객체
	 */

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

	/**
	 * 주문 상세 번호로 주문 문자열(코드)을 조회합니다.
	 *
	 * @param orderDetailId 주문 상세 번호
	 * @return 주문 문자열(코드)
	 */

	@Transactional(readOnly = true)
	public String readOrderStr(long orderDetailId) {
		return orderDetailRepository.findOrderStrByOrderDetailId(orderDetailId);
	}
}
