package store.buzzbook.core.service.order;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateOrderStatusRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.order.UpdateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.dto.order.UpdateOrderStatusRequest;
import store.buzzbook.core.dto.order.UpdateWrappingRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.product.Product;
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
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UserRepository userRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final WrappingRepository wrappingRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final UserService userService;


	public Map<String, Object> readOrders(ReadOrderRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadOrderResponse> pageOrders = orderRepository.findAll(request, pageable);
		List<ReadOrderResponse> orders = pageOrders.getContent();
		List<ReadOrderResponse> responses = new ArrayList<>();

		for (ReadOrderResponse order : orders) {
			List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
			List<ReadOrderDetailResponse> details = new ArrayList<>();

			for (OrderDetail orderDetail : orderDetails) {
				details.add(OrderDetailMapper.toDto(orderDetail));
			}

			responses.add(order);
		}

		data.put("responseData", responses);
		data.put("total", pageOrders.getTotalElements());

		return data;
	}

	public Map<String, Object> readMyOrders(ReadOrderRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		userRepository.findByLoginId(request.getLoginId())
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Page<ReadOrderResponse> pageOrders = orderRepository.findAllByUser_LoginId(request, pageable);
		List<ReadOrderResponse> orders = pageOrders.getContent();

		List<ReadOrderResponse> responses = new ArrayList<>();

		for (ReadOrderResponse order : orders) {
			List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
			List<ReadOrderDetailResponse> details = new ArrayList<>();

			for (OrderDetail orderDetail : orderDetails) {
				details.add(OrderDetailMapper.toDto(orderDetail));
			}

			responses.add(order);
		}

		data.put("responseData", responses);
		data.put("total", pageOrders.getTotalElements());

		return data;
	}

	public ReadOrderResponse readOrder(long orderId, String loginId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(orderId, loginId);
		List<ReadOrderDetailResponse> details = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			// details.add(OrderDetailMapper.toDto(orderDetail, readOrderResponse));
		}

		return OrderMapper.toDto(order, details, loginId);
	}

	@Transactional
	public ReadOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		log.warn("OrderService createOrder {}", createOrderRequest.getDeliveryPolicyId());

		List<CreateOrderDetailRequest> details = createOrderRequest.getDetails();

		UserInfo userInfo = userService.getUserInfoByLoginId(createOrderRequest.getLoginId());

		User user = userRepository.findById(userInfo.id())
			.orElseThrow(() -> new IllegalArgumentException("User not found"));
		Order order = orderRepository.save(OrderMapper.toEntity(createOrderRequest, user));

		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (CreateOrderDetailRequest detail : details) {
			detail.setOrderId(order.getId());
			OrderStatus orderStatus = orderStatusRepository.findById(detail.getOrderStatusId())
				.orElseThrow(()-> new IllegalArgumentException("Order Status not found"));
			Wrapping wrapping = null;
			if (!detail.isWrap()) {
				wrapping = wrappingRepository.findById(1)
					.orElseThrow(()-> new IllegalArgumentException("Wrapping not found"));
			}

			Product product = productRepository.findById(detail.getProductId())
				.orElseThrow(()-> new IllegalArgumentException("Product not found"));
			OrderDetail orderDetail = OrderDetailMapper.toEntity(detail, order, wrapping, product, orderStatus);
			orderDetail = orderDetailRepository.save(orderDetail);
			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail));
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, user.getLoginId());
	}

	public ReadOrderResponse updateOrderWithAdmin(UpdateOrderRequest updateOrderRequest) {
		Order order = orderRepository.findById(updateOrderRequest.getId())
			.orElseThrow(()-> new IllegalArgumentException("Order not found"));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(updateOrderRequest.getId());
		List<ReadOrderDetailResponse> readOrderDetailRespons = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails) {
			for (int orderStatusId : updateOrderRequest.getDetails().stream().filter(d-> orderDetail.getOrder().getId() == updateOrderRequest.getId()).map(
				UpdateOrderDetailRequest::getOrderStatusId).toList()) {
				orderDetail.setOrderStatus(orderStatusRepository.findById(orderStatusId).orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
				readOrderDetailRespons.add(OrderDetailMapper.toDto(orderDetailRepository.save(orderDetail)));
			}
		}
		return OrderMapper.toDto(order, readOrderDetailRespons, updateOrderRequest.getUser().loginId());
	}

	public ReadOrderResponse updateOrder(UpdateOrderRequest updateOrderRequest) {
		Order order = orderRepository.findById(updateOrderRequest.getId())
			.orElseThrow(()-> new IllegalArgumentException("Order not found"));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(updateOrderRequest.getId(), updateOrderRequest.getUser().loginId());
		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails) {
			for (int orderStatusId : updateOrderRequest.getDetails().stream().filter(d-> orderDetail.getOrder().getId() == updateOrderRequest.getId()).map(
				UpdateOrderDetailRequest::getOrderStatusId).toList()) {
				orderDetail.setOrderStatus(orderStatusRepository.findById(orderStatusId).orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
				readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetailRepository.save(orderDetail)));
			}
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, updateOrderRequest.getUser().loginId());
	}

	public List<ReadOrderDetailResponse> readOrderDetails(long orderId) {
		List<ReadOrderDetailResponse> readOrderDetailRespons = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetailRepository.findAllByOrder_Id(orderId)) {
			readOrderDetailRespons.add(OrderDetailMapper.toDto(orderDetail));
		}

		return readOrderDetailRespons;
	}

	public ReadOrderStatusResponse createOrderStatus(CreateOrderStatusRequest createOrderStatusRequest) {

		return OrderStatusMapper.toDto(orderStatusRepository.save(OrderStatus.builder().name(createOrderStatusRequest.getName()).updateAt(
			LocalDateTime.now()).build()));
	}

	public ReadOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest updateOrderStatusRequest) {

		return OrderStatusMapper.toDto(orderStatusRepository.save(OrderStatus.builder().id(updateOrderStatusRequest.getId()).name(updateOrderStatusRequest.getName()).build()));
	}

	public void deleteOrderStatus(int orderStatusId) {
		orderStatusRepository.delete(orderStatusRepository.findById(orderStatusId).orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
	}

	public ReadOrderStatusResponse readOrderStatusById(int id) {
		return OrderStatusMapper.toDto(orderStatusRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
	}

	public ReadOrderStatusResponse readOrderStatusByName(String orderStatusName) {
		return OrderStatusMapper.toDto(orderStatusRepository.findByName(orderStatusName));
	}

	public List<ReadOrderStatusResponse> readAllOrderStatus() {
		return orderStatusRepository.findAll().stream().map(OrderStatusMapper::toDto).toList();
	}

	public ReadDeliveryPolicyResponse createDeliveryPolicy(CreateDeliveryPolicyRequest createDeliveryPolicyRequest) {
		return DeliveryPolicyMapper.toDto(deliveryPolicyRepository.save(DeliveryPolicy.builder().name(createDeliveryPolicyRequest.getName())
			.policyPrice(createDeliveryPolicyRequest.getPolicyPrice()).standardPrice(createDeliveryPolicyRequest.getStandardPrice()).build()));
	}

	public ReadDeliveryPolicyResponse updateDeliveryPolicy(UpdateDeliveryPolicyRequest updateDeliveryPolicyRequest) {
		return DeliveryPolicyMapper.toDto(deliveryPolicyRepository.save(DeliveryPolicy.builder().id(updateDeliveryPolicyRequest.getId())
			.name(updateDeliveryPolicyRequest.getName()).policyPrice(updateDeliveryPolicyRequest.getPolicyPrice())
			.standardPrice(updateDeliveryPolicyRequest.getStandardPrice()).build()));
	}

	public void deleteDeliveryPolicy(int deliveryPolicyId) {
		deliveryPolicyRepository.deleteById(deliveryPolicyId);
	}

	public ReadDeliveryPolicyResponse readDeliveryPolicyById(int deliveryPolicyId) {
		return DeliveryPolicyMapper.toDto(deliveryPolicyRepository.findById(deliveryPolicyId).orElseThrow(() -> new IllegalArgumentException("Delivery Policy not found")));
	}

	public List<ReadDeliveryPolicyResponse> readAllDeliveryPolicy() {
		return deliveryPolicyRepository.findAll().stream().map(DeliveryPolicyMapper::toDto).toList();
	}

	public ReadWrappingResponse createWrapping(CreateWrappingRequest createWrappingRequest) {
		return WrappingMapper.toDto(wrappingRepository.save(Wrapping.builder().paper(createWrappingRequest.getPaper())
			.price(createWrappingRequest.getPrice()).build()));
	}

	public ReadWrappingResponse updateWrapping(UpdateWrappingRequest updateWrappingRequest) {
		return WrappingMapper.toDto(wrappingRepository.save(Wrapping.builder().id(updateWrappingRequest.getId())
			.price(updateWrappingRequest.getPrice()).paper(updateWrappingRequest.getPaper()).build()));
	}

	public void deleteWrapping(int wrappingId) {
		wrappingRepository.deleteById(wrappingId);
	}

	public ReadWrappingResponse readWrappingById(int wrappingId) {
		return WrappingMapper.toDto(wrappingRepository.findById(wrappingId).orElseThrow(() -> new IllegalArgumentException("Wrapping not found")));
	}

	public List<ReadWrappingResponse> readAllWrapping() {
		return wrappingRepository.findAll().stream().map(WrappingMapper::toDto).toList();
	}
}
