package store.buzzbook.core.service.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.order.AddressNotFoundException;
import store.buzzbook.core.common.exception.order.DeliveryPolicyNotFoundException;
import store.buzzbook.core.common.exception.order.OrderDetailNotFoundException;
import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.common.exception.order.OrderStatusNotFoundException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.WrappingNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.order.CreateDeliveryPolicyRequest;
import store.buzzbook.core.dto.order.CreateOrderDetailRequest;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.CreateOrderStatusRequest;
import store.buzzbook.core.dto.order.CreateWrappingRequest;
import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderProjectionResponse;
import store.buzzbook.core.dto.order.ReadOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderWithoutLoginRequest;
import store.buzzbook.core.dto.order.ReadOrdersRequest;
import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.order.ReadOrdersResponse;
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
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.user.AddressRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
	private static final int UNPACKAGED = 1;
	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final UserRepository userRepository;
	private final DeliveryPolicyRepository deliveryPolicyRepository;
	private final WrappingRepository wrappingRepository;
	private final ProductRepository productRepository;
	private final OrderStatusRepository orderStatusRepository;
	private final UserService userService;
	private final AddressRepository addressRepository;

	public Map<String, Object> readOrders(ReadOrdersRequest request) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadOrderProjectionResponse> pageOrders = orderRepository.findAll(request, pageable);
		List<ReadOrderProjectionResponse> orders = pageOrders.getContent();
		List<ReadOrdersResponse> responses = new ArrayList<>();

		Set<String> orderStrs = orders.stream().map(ReadOrderProjectionResponse::getOrderStr).collect(Collectors.toSet());
		for (String orderStr : orderStrs) {
			Iterator<ReadOrderProjectionResponse> iterator = orders.stream().filter(order -> order.getOrderStr().equals(orderStr)).iterator();
			Optional<ReadOrderProjectionResponse> optionalOrder = orders.stream().filter(o->o.getOrderStr().equals(orderStr)).findFirst();
			ReadOrderProjectionResponse order = optionalOrder.orElseThrow(() -> new OrderNotFoundException("Order not found"));
			ReadOrdersResponse readOrdersResponse = ReadOrdersResponse.builder().id(order.getId()).orderStr(order.getOrderStr()).address(order.getAddress())
				.addressDetail(order.getAddressDetail()).zipcode(order.getZipcode()).receiver(order.getReceiver()).deliveryRate(order.getDeliveryRate())
				.request(order.getRequest()).loginId(order.getLoginId()).desiredDeliveryDate(order.getDesiredDeliveryDate()).orderEmail(order.getOrderEmail())
				.sender(order.getSender()).receiverContactNumber(order.getReceiverContactNumber()).senderContactNumber(order.getSenderContactNumber()).couponCode(order.getCouponCode()).build();
			List<ReadOrderDetailProjectionResponse> details = new ArrayList<>();
			while (iterator.hasNext()) {
				ReadOrderProjectionResponse newOrder = iterator.next();
				if (orderStr.equals(newOrder.getOrderStr())) {
					details.add(newOrder.getOrderDetail());
				}
			}
			readOrdersResponse.setDetails(details);
			responses.add(readOrdersResponse);
		}

		data.put("responseData", responses);
		data.put("total", responses.size());

		return data;
	}

	public Map<String, Object> readMyOrders(ReadOrdersRequest request, String loginId) {
		Map<String, Object> data = new HashMap<>();
		PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize());

		Page<ReadOrderProjectionResponse> pageOrders = orderRepository.findAllByUser_LoginId(request, loginId, pageable);
		List<ReadOrderProjectionResponse> orders = pageOrders.getContent();
		List<ReadOrdersResponse> responses = new ArrayList<>();

		Set<String> orderStrs = orders.stream().map(ReadOrderProjectionResponse::getOrderStr).collect(Collectors.toSet());
		for (String orderStr : orderStrs) {
			Iterator<ReadOrderProjectionResponse> iterator = orders.stream().filter(order -> order.getOrderStr().equals(orderStr)).iterator();
			Optional<ReadOrderProjectionResponse> optionalOrder = orders.stream().filter(o->o.getOrderStr().equals(orderStr)).findFirst();
			ReadOrderProjectionResponse order = optionalOrder.orElseThrow(() -> new OrderNotFoundException("Order not found"));
			ReadOrdersResponse readOrdersResponse = ReadOrdersResponse.builder().id(order.getId()).orderStr(order.getOrderStr()).address(order.getAddress())
				.addressDetail(order.getAddressDetail()).zipcode(order.getZipcode()).receiver(order.getReceiver()).deliveryRate(order.getDeliveryRate())
				.request(order.getRequest()).loginId(order.getLoginId()).desiredDeliveryDate(order.getDesiredDeliveryDate())
				.sender(order.getSender()).receiverContactNumber(order.getReceiverContactNumber()).senderContactNumber(order.getSenderContactNumber()).couponCode(order.getCouponCode()).build();
			List<ReadOrderDetailProjectionResponse> details = new ArrayList<>();
			while (iterator.hasNext()) {
				ReadOrderProjectionResponse newOrder = iterator.next();
				if (orderStr.equals(newOrder.getOrderStr())) {
					details.add(newOrder.getOrderDetail());
				}
			}
			readOrdersResponse.setDetails(details);
			responses.add(readOrdersResponse);
		}

		data.put("responseData", responses);
		data.put("total", responses.size());

		return data;
	}

	@Transactional
	public ReadOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
		List<CreateOrderDetailRequest> details = createOrderRequest.getDetails();
		User user = null;
		if (createOrderRequest.getLoginId() != null && !createOrderRequest.getLoginId().isBlank()) {
			UserInfo userInfo = userService.getUserInfoByLoginId(createOrderRequest.getLoginId());

			user = userRepository.findById(userInfo.id()).orElseThrow(()->new UserNotFoundException("User not found"));
		}

		Order order = null;

		if (createOrderRequest.getAddress().isEmpty()) {
			Optional<Address> address = addressRepository.findById(Long.parseLong(createOrderRequest.getAddresses()));
			if (address.isPresent()) {
				order = orderRepository.save(OrderMapper.toEntityWithAddress(createOrderRequest, user, address.get()));
			} else {
				throw new AddressNotFoundException("Address not found");
			}

		} else {
			order = orderRepository.save(OrderMapper.toEntity(createOrderRequest, user));
		}


		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (CreateOrderDetailRequest detail : details) {
			detail.setOrderId(order.getId());
			OrderStatus orderStatus = orderStatusRepository.findById(detail.getOrderStatusId())
				.orElseThrow(() -> new OrderStatusNotFoundException("Order Status not found"));
			Wrapping wrapping = null;
			if (detail.getWrappingId() != 0) {
				wrapping = wrappingRepository.findById(detail.getWrappingId())
					.orElseThrow(() -> new WrappingNotFoundException("Wrapping not found"));
			} else {
				wrapping = wrappingRepository.findById(UNPACKAGED)
					.orElseThrow(() -> new WrappingNotFoundException("Wrapping not found"));
			}

			Product product = productRepository.findById(detail.getProductId())
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

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

	public ReadOrderResponse updateOrderWithAdmin(UpdateOrderRequest updateOrderRequest, String loginId) {
		Order order = orderRepository.findByOrderStr(updateOrderRequest.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails)
		{
			orderDetailRepository.save(OrderDetail.builder().orderStatus(orderStatusRepository.findByName(updateOrderRequest.getOrderStatusName()))
				.id(orderDetail.getId())
				.wrap(orderDetail.isWrap())
				.createAt(orderDetail.getCreateAt())
				.price(orderDetail.getPrice())
				.quantity(orderDetail.getQuantity())
				.order(orderDetail.getOrder())
				.wrapping(orderDetail.getWrapping())
				.product(orderDetail.getProduct())
				.build());

			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}
		return OrderMapper.toDto(order, readOrderDetailResponse, loginId);
	}

	public ReadOrderResponse updateOrder(UpdateOrderRequest updateOrderRequest, String loginId) {
		Order order = orderRepository.findByOrderStr(updateOrderRequest.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_User_LoginId(
			order.getId(), loginId);
		List<ReadOrderDetailResponse> readOrderDetailResponse = new ArrayList<>();

		for (OrderDetail orderDetail : orderDetails)
		{
			orderDetailRepository.save(OrderDetail.builder().orderStatus(orderStatusRepository.findByName(updateOrderRequest.getOrderStatusName()))
				.id(orderDetail.getId())
				.wrap(orderDetail.isWrap())
				.createAt(orderDetail.getCreateAt())
				.price(orderDetail.getPrice())
				.quantity(orderDetail.getQuantity())
				.order(orderDetail.getOrder())
				.wrapping(orderDetail.getWrapping())
				.product(orderDetail.getProduct())
				.build());

			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			readOrderDetailResponse.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}
		return OrderMapper.toDto(order, readOrderDetailResponse, loginId);
	}

	public ReadOrderResponse readOrder(ReadOrderRequest request, String loginId) {
		Order order = orderRepository.findByOrderStr(request.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_Id(order.getId());
		List<ReadOrderDetailResponse> details = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new IllegalArgumentException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			details.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return OrderMapper.toDto(order, details, loginId);
	}

	public ReadOrderResponse readOrderWithoutLogin(ReadOrderWithoutLoginRequest request) {
		Order order = orderRepository.findByOrderStr(request.getOrderId());
		List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_OrderEmail(order.getId(),
			request.getOrderEmail());
		List<ReadOrderDetailResponse> details = new ArrayList<>();
		for (OrderDetail orderDetail : orderDetails) {
			Product product = productRepository.findById(orderDetail.getProduct().getId())
				.orElseThrow(() -> new ProductNotFoundException("Product not found"));

			ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

			Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new WrappingNotFoundException("Wrapping not found"));
			ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

			details.add(OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse));
		}

		return OrderMapper.toDto(order, details, null);
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
			.orElseThrow(() -> new OrderStatusNotFoundException("Order Status not found")));
	}

	public ReadOrderStatusResponse readOrderStatusById(int id) {
		return OrderStatusMapper.toDto(orderStatusRepository.findById(id)
			.orElseThrow(() -> new OrderStatusNotFoundException("Order Status not found")));
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
			.orElseThrow(() -> new DeliveryPolicyNotFoundException("Delivery Policy not found")));
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
			.orElseThrow(() -> new WrappingNotFoundException("Wrapping not found")));
	}

	public List<ReadWrappingResponse> readAllWrapping() {
		return wrappingRepository.findAll().stream().map(WrappingMapper::toDto).toList();
	}

	@Transactional
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
			.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		if (request.getOrderStatusName().equals("CANCELED") || request.getOrderStatusName().equals("PARTIAL_CANCELED")
			|| request.getOrderStatusName().equals("REFUND") || request.getOrderStatusName().equals("PARTIAL_REFUND")) {
			product.increaseStock(orderDetail.getQuantity());
		}

		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new WrappingNotFoundException("Wrapping not found"));
		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

		ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

		return OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse);
	}

	public ReadOrderDetailResponse updateOrderDetailWithAdmin(UpdateOrderDetailRequest request) {
		OrderDetail orderDetail = orderDetailRepository.findById(request.getId()).orElseThrow(()-> new OrderDetailNotFoundException("Order Detail not found"));
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
			.orElseThrow(() -> new ProductNotFoundException("Product not found"));

		Wrapping wrapping = wrappingRepository.findById(orderDetail.getWrapping().getId()).orElseThrow(() -> new WrappingNotFoundException("Wrapping not found"));
		ReadWrappingResponse readWrappingResponse = WrappingMapper.toDto(wrapping);

		ProductResponse productResponse = ProductResponse.convertToProductResponse(product);

		return OrderDetailMapper.toDto(orderDetail, productResponse, readWrappingResponse);
	}

	public String readOrderStr(long orderDetailId) {
		return orderDetailRepository.findOrderStrByOrderDetailId(orderDetailId);
	}
}
