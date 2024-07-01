package store.buzzbook.core.service.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateOrderStatusRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.dto.order.UpdateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.dto.order.UpdateOrderStatusRequest;
import store.buzzbook.core.dto.order.UpdateWrappingRequest;
import store.buzzbook.core.dto.product.ProductResponse;
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

	public Map<String, Object> readOrders(ReadOrdersRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadOrderProjectionResponse> pageOrders = orderRepository.findAll(request, pageable);
		List<ReadOrderProjectionResponse> orders = pageOrders.getContent();

		data.put("responseData", orders);
		data.put("total", pageOrders.getTotalElements());

		return data;
	}

	public Map<String, Object> readMyOrders(ReadOrdersRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadOrderProjectionResponse> pageOrders = orderRepository.findAllByUser_LoginId(request, pageable);
		List<ReadOrderProjectionResponse> orders = pageOrders.getContent();

		data.put("responseData", orders);
		data.put("total", pageOrders.getTotalElements());

		return data;
	}

	@Transactional
	public ReadOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		List<CreateOrderDetailRequest> details = createOrderRequest.getDetails();

		UserInfo userInfo = userService.getUserInfoByLoginId(createOrderRequest.getLoginId()); //null 이면 (비회원)

		User user = userRepository.findById(userInfo.id()).get();

		Order order = orderRepository.save(OrderMapper.toEntity(createOrderRequest, user));

		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (CreateOrderDetailRequest detail : details) {
			detail.setOrderId(order.getId());
			OrderStatus orderStatus = orderStatusRepository.findById(detail.getOrderStatusId())
				.orElseThrow(() -> new IllegalArgumentException("Order Status not found"));
			Wrapping wrapping = null;

			wrapping = wrappingRepository.findById(1)
				.orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));

			Product product = productRepository.findById(detail.getProductId())
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

			detail.setPrice(product.getPrice());

			OrderDetail orderDetail = OrderDetailMapper.toEntity(detail, order, wrapping, product, orderStatus);
			orderDetail = orderDetailRepository.save(orderDetail);

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, user.getLoginId());
	}

	public ReadOrderResponse updateOrderWithAdmin(UpdateOrderRequest updateOrderRequest) {
		Order order = orderRepository.findById(updateOrderRequest.getId())
			.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(updateOrderRequest.getId());
		List<ReadOrderDetailResponse> readOrderDetailRespons = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails) {
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);
			for (int orderStatusId : updateOrderRequest.getDetails()
				.stream()
				.filter(d -> orderDetail.getOrder().getId() == updateOrderRequest.getId())
				.map(
					UpdateOrderDetailRequest::getOrderStatusId)
				.toList()) {
				orderDetail.setOrderStatus(orderStatusRepository.findById(orderStatusId)
					.orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
				readOrderDetailRespons.add(
					OrderDetailMapper.toDto(orderDetailRepository.save(orderDetail), productResponse, readWrappingResponse));
			}
		}
		return OrderMapper.toDto(order, readOrderDetailRespons, updateOrderRequest.getLoginId());
	}

	public ReadOrderResponse updateOrder(UpdateOrderRequest updateOrderRequest) {
		Order order = orderRepository.findById(updateOrderRequest.getId())
			.orElseThrow(() -> new IllegalArgumentException("Order not found"));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(
			updateOrderRequest.getId(), updateOrderRequest.getLoginId());
		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails) {
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);
			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);
			for (int orderStatusId : updateOrderRequest.getDetails()
				.stream()
				.filter(d -> orderDetail.getOrder().getId() == updateOrderRequest.getId())
				.map(
					UpdateOrderDetailRequest::getOrderStatusId)
				.toList()) {
				orderDetail.setOrderStatus(orderStatusRepository.findById(orderStatusId)
					.orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
				readOrderDetailResponse.add(
					OrderDetailMapper.toDto(orderDetailRepository.save(orderDetail), productResponse, readWrappingResponse));
			}
		}

		return OrderMapper.toDto(order, readOrderDetailResponse, updateOrderRequest.getLoginId());
	}

	public ReadOrderResponse readOrder(ReadOrderRequest request) {
		Order order = orderRepository.findByOrderStr(request.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(order.getId(),
			request.getLoginId());
		List<ReadOrderDetailResponse> details = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			details.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return OrderMapper.toDto(order, details, request.getLoginId());
	}

	public ReadOrderStatusResponse createOrderStatus(CreateOrderStatusRequest createOrderStatusRequest) {

		return OrderStatusMapper.toDto(
			orderStatusRepository.save(OrderStatus.builder().name(createOrderStatusRequest.getName()).updateAt(
				LocalDateTime.now()).build()));
	}

	public ReadOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest updateOrderStatusRequest) {

		return OrderStatusMapper.toDto(orderStatusRepository.save(OrderStatus.builder()
			.id(updateOrderStatusRequest.getId())
			.name(updateOrderStatusRequest.getName())
			.build()));
	}

	public void deleteOrderStatus(int orderStatusId) {
		orderStatusRepository.delete(orderStatusRepository.findById(orderStatusId)
			.orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
	}

	public ReadOrderStatusResponse readOrderStatusById(int id) {
		return OrderStatusMapper.toDto(orderStatusRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Order Status not found")));
	}

	public ReadOrderStatusResponse readOrderStatusByName(String orderStatusName) {
		return OrderStatusMapper.toDto(orderStatusRepository.findByName(orderStatusName));
	}

	public List<ReadOrderStatusResponse> readAllOrderStatus() {
		return orderStatusRepository.findAll().stream().map(OrderStatusMapper::toDto).toList();
	}

	public ReadDeliveryPolicyResponse createDeliveryPolicy(CreateDeliveryPolicyRequest createDeliveryPolicyRequest) {
		return DeliveryPolicyMapper.toDto(
			deliveryPolicyRepository.save(DeliveryPolicy.builder()
				.name(createDeliveryPolicyRequest.getName())
				.policyPrice(createDeliveryPolicyRequest.getPolicyPrice())
				.standardPrice(createDeliveryPolicyRequest.getStandardPrice())
				.build()));
	}

	public ReadDeliveryPolicyResponse updateDeliveryPolicy(UpdateDeliveryPolicyRequest updateDeliveryPolicyRequest) {
		return DeliveryPolicyMapper.toDto(
			deliveryPolicyRepository.save(DeliveryPolicy.builder().id(updateDeliveryPolicyRequest.getId())
				.name(updateDeliveryPolicyRequest.getName()).policyPrice(updateDeliveryPolicyRequest.getPolicyPrice())
				.standardPrice(updateDeliveryPolicyRequest.getStandardPrice()).build()));
	}

	public void deleteDeliveryPolicy(int deliveryPolicyId) {
		deliveryPolicyRepository.deleteById(deliveryPolicyId);
	}

	public ReadDeliveryPolicyResponse readDeliveryPolicyById(int deliveryPolicyId) {
		return DeliveryPolicyMapper.toDto(deliveryPolicyRepository.findById(deliveryPolicyId)
			.orElseThrow(() -> new IllegalArgumentException("Delivery Policy not found")));
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
		return WrappingMapper.toDto(wrappingRepository.findById(wrappingId)
			.orElseThrow(() -> new IllegalArgumentException("Wrapping not found")));
	}

	public List<ReadWrappingResponse> readAllWrapping() {
		return wrappingRepository.findAll().stream().map(WrappingMapper::toDto).toList();
	}
}
