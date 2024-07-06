package store.buzzbook.core.mapper.order;

import java.time.LocalDate;
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
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


	public static ReadOrderResponse toDto(Order order, List<ReadOrderDetailResponse> details, String loginId) {
		return ReadOrderResponse.builder()
			.id(order.getId())
			.orderStr(order.getOrderStr())
			.details(details)
			.price(order.getPrice())
			.address(order.getAddress())
			.addressDetail(order.getAddressDetail())
			.desiredDeliveryDate(order.getDesiredDeliveryDate())
			.receiver(order.getReceiver())
			.request(order.getRequest())
			.zipcode(order.getZipcode())
			.loginId(loginId)
			.senderContactNumber(order.getSenderContactNumber())
			.receiverContactNumber(order.getReceiverContactNumber())
			.sender(order.getSender())
			.orderEmail(order.getOrderPassword())
			.build();
	}

	public static Order toEntity(CreateOrderRequest createOrderRequest, User user) {

		return Order.builder()
			.user(user)
			.orderStr(createOrderRequest.getOrderStr())
			.price(createOrderRequest.getPrice())
			.request(createOrderRequest.getRequest())
			.receiver(createOrderRequest.getReceiver())
			.zipcode(createOrderRequest.getZipcode())
			.address(createOrderRequest.getAddress())
			.addressDetail(createOrderRequest.getAddressDetail())
			.desiredDeliveryDate(LocalDate.parse(createOrderRequest.getDesiredDeliveryDate(), formatter))
			.sender(createOrderRequest.getSender())
			.receiverContactNumber(createOrderRequest.getReceiverContactNumber())
			.senderContactNumber(createOrderRequest.getContactNumber())
			.orderPassword(createOrderRequest.getOrderPassword())
			.build();
	}
}
