package store.buzzbook.core.mapper.order;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.user.User;

public class OrderMapper {
	public static ZonedDateTime toZonedDateTime(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
		return ZonedDateTime.parse(date, formatter);
	}


	public static ReadOrderResponse toDto(Order order, List<ReadOrderDetailResponse> details, String loginId) {
		return ReadOrderResponse.builder()
			.id(order.getId())
			.orderStr(order.getOrderStr())
			.details(details)
			.price(order.getPrice())
			.address(order.getAddress())
			.addressDetail(order.getAddressDetail())
			.deliveryPolicy(DeliveryPolicyMapper.toDto(order.getDeliveryPolicy()))
			.desiredDeliveryDate(ZonedDateTimeParser.toStringDateTime(order.getDesiredDeliveryDate()))
			.receiver(order.getReceiver())
			.request(order.getRequest())
			.zipcode(order.getZipcode())
			.loginId(loginId)
			.build();
	}

	public static Order toEntity(CreateOrderRequest createOrderRequest, DeliveryPolicy deliveryPolicy, User user) {

		return Order.builder()
			.user(user)
			.orderStr(createOrderRequest.getOrderStr())
			.price(createOrderRequest.getPrice())
			.deliveryPolicy(deliveryPolicy)
			.request(createOrderRequest.getRequest())
			.receiver(createOrderRequest.getReceiver())
			.zipcode(createOrderRequest.getZipcode())
			.address(createOrderRequest.getAddress())
			.addressDetail(createOrderRequest.getAddressDetail())
			.desiredDeliveryDate(ZonedDateTimeParser.toDateTime(createOrderRequest.getDesiredDeliveryDate()))
			.build();
	}
}
