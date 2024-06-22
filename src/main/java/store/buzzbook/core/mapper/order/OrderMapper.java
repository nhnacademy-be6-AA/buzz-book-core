package store.buzzbook.core.mapper.order;

import java.util.List;

import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.user.User;

public class OrderMapper {
	public static ReadOrderResponse toDto(Order order, List<ReadOrderDetailResponse> details) {
		return ReadOrderResponse.builder()
			.id(order.getId())
			.details(details)
			.price(order.getPrice())
			.address(order.getAddress())
			.addressDetail(order.getAddressDetail())
			.deliveryPolicy(DeliveryPolicyMapper.toDto(order.getDeliveryPolicy()))
			.desiredDeliveryDate(order.getDesiredDeliveryDate())
			.receiver(order.getReceiver())
			.request(order.getRequest())
			.zipcode(order.getZipcode())
			.build();
	}

	public static Order toEntity(CreateOrderRequest createOrderRequest, DeliveryPolicy deliveryPolicy, User user) {
		return Order.builder()
			.user(user)
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
