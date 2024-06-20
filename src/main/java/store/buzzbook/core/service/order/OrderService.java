package store.buzzbook.core.service.order;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.OrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.UpdateOrderDetailRequest;
import store.buzzbook.core.dto.order.UpdateOrderRequest;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.order.Wrapping;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.mapper.order.OrderDetailMapper;
import store.buzzbook.core.mapper.order.OrderMapper;
import store.buzzbook.core.repository.order.DeliveryPolicyRepository;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.order.OrderRepository;
import store.buzzbook.core.repository.order.OrderStatusRepository;
import store.buzzbook.core.repository.order.WrappingRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UserRepository userRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final WrappingRepository wrappingRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;

	public Page<ReadOrderResponse> readOrders(Pageable pageable) {
		Page<Order> orders = orderRepository.findAll(pageable);
		List<ReadOrderResponse> responses = new ArrayList<>();

		for (Order order : orders) {
			List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
			List<OrderDetailResponse> details = new ArrayList<>();

			for (OrderDetail orderDetail : orderDetails) {
				details.add(OrderDetailMapper.toDto(orderDetail));
			}
			responses.add(OrderMapper.toDto(order, details));
		}

		return new PageImpl<>(responses, pageable, orders.getTotalElements());
	}

	public Page<ReadOrderResponse> readMyOrders(long userId, Pageable pageable) {
		userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Page<Order> orders = orderRepository.findByUser_Id(userId, pageable);
		List<ReadOrderResponse> responses = new ArrayList<>();

		for (Order order : orders) {
			List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
			List<OrderDetailResponse> details = new ArrayList<>();

			for (OrderDetail orderDetail : orderDetails) {
				details.add(OrderDetailMapper.toDto(orderDetail));
			}
			responses.add(OrderMapper.toDto(order, details));
		}

		return new PageImpl<>(responses, pageable, orders.getTotalElements());
	}

	@Transactional
	public ReadOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.findById(createOrderRequest
			.getDeliveryPolicyId()).orElseThrow(()-> new IllegalArgumentException("Delivery Policy not found"));

		List<CreateOrderDetailRequest> details = createOrderRequest.getDetails();

		Order order = OrderMapper.toEntity(createOrderRequest, deliveryPolicy);

		order = orderRepository.save(order);

		List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

		for (CreateOrderDetailRequest detail : details) {
			OrderStatus orderStatus = orderStatusRepository.findById(detail.getOrderStatusId())
				.orElseThrow(()-> new IllegalArgumentException("Order Status not found"));
			Wrapping wrapping = wrappingRepository.findById(detail.getWrappingId())
				.orElseThrow(()-> new IllegalArgumentException("Wrapping not found"));
			Product product = productRepository.findById(detail.getProductId())
				.orElseThrow(()-> new IllegalArgumentException("Product not found"));
			OrderDetail orderDetail = OrderDetailMapper.toEntity(detail, order, wrapping, product, orderStatus);
			orderDetail = orderDetailRepository.save(orderDetail);
			orderDetailResponses.add(OrderDetailMapper.toDto(orderDetail));
		}

		return OrderMapper.toDto(order, orderDetailResponses);
	}

	public ReadOrderResponse updateOrder(UpdateOrderRequest updateOrderRequest) {
		Order order = orderRepository.findById(updateOrderRequest.getId())
			.orElseThrow(()-> new IllegalArgumentException("Order not found"));
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(updateOrderRequest.getId());
		List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails) {
			for (int orderStatusId : updateOrderRequest.getDetails().stream().filter(d-> orderDetail.getId() == updateOrderRequest.getId()).map(
				UpdateOrderDetailRequest::getOrderStatusId).toList()) {
				orderDetail.setOrderStatus(orderStatusRepository.findById(orderStatusId).get());
				orderDetailResponses.add(OrderDetailMapper.toDto(orderDetailRepository.save(orderDetail)));
			}
		}

		return OrderMapper.toDto(order, orderDetailResponses);
	}

}
