package store.buzzbook.core.mapper.order;

import java.util.List;

import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.OrderDetailResponse;
import store.buzzbook.core.dto.order.OrderReadResponse;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;

public class OrderMapper {
	public static OrderReadResponse toDto(Order order, List<OrderDetailResponse> details) {
		return OrderReadResponse.builder()
			.id(order.getId())
			.details(details)
			.price(order.getPrice())
			.address(order.getAddress())
			.addressDetail(order.getAddressDetail())
			.deliveryPolicy(order.getDeliveryPolicy())
			.desiredDeliveryDate(order.getDesiredDeliveryDate())
			.receiver(order.getReceiver())
			.request(order.getRequest())
			.zipcode(order.getZipcode())
			.build();
	}

	public static Order toEntity(CreateOrderRequest createOrderRequest, DeliveryPolicy deliveryPolicy) {
		return Order.builder()
			.user(createOrderRequest.getUser())
			.price(createOrderRequest.getPrice())
			.deliveryPolicy(deliveryPolicy)
			.request(createOrderRequest.getRequest())
			.receiver(createOrderRequest.getReceiver())
			.zipcode(createOrderRequest.getZipcode())
			.address(createOrderRequest.getAddress())
			.addressDetail(createOrderRequest.getAddressDetail())
			.desiredDeliveryDate(createOrderRequest.getDesiredDeliveryDate())
			.build();
	}
}
