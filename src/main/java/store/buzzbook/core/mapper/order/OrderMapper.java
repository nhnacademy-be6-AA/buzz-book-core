package store.buzzbook.core.mapper.order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import store.buzzbook.core.dto.order.CreateOrderRequest;
import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderStatus;
import store.buzzbook.core.entity.user.Address;
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
			.desiredDeliveryDate(order.getDesiredDeliveryDate().format(formatter))
			.receiver(order.getReceiver())
			.request(order.getRequest())
			.zipcode(order.getZipcode())
			.loginId(loginId)
			.senderContactNumber(order.getSenderContactNumber())
			.receiverContactNumber(order.getReceiverContactNumber())
			.sender(order.getSender())
			.orderEmail(order.getOrderEmail())
			.couponCode(order.getCouponCode())
			.deliveryRate(order.getDeliveryRate())
			.deductedPoints(order.getDeductedPoints())
			.earnedPoints(order.getEarnedPoints())
			.deductedCouponPrice(order.getDeductedCouponPrice())
			.orderStatus(order.getOrderStatus().getName())
			.build();
	}

	public static Order toEntity(CreateOrderRequest createOrderRequest, OrderStatus orderStatus, User user) {
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
			.orderEmail(createOrderRequest.getOrderEmail())
			.couponCode(createOrderRequest.getCouponCode())
			.deliveryRate(createOrderRequest.getDeliveryRate())
			.orderStatus(orderStatus)
			.deductedPoints(createOrderRequest.getDeductedPoints())
			.earnedPoints(createOrderRequest.getEarnedPoints())
			.deductedCouponPrice(createOrderRequest.getDeductedCouponPrice())
			.build();
	}

	public static Order toEntityWithAddress(CreateOrderRequest createOrderRequest, User user, OrderStatus orderStatus, Address address) {

		return Order.builder()
			.user(user)
			.orderStr(createOrderRequest.getOrderStr())
			.price(createOrderRequest.getPrice())
			.request(createOrderRequest.getRequest())
			.receiver(createOrderRequest.getReceiver())
			.zipcode(address.getZipcode())
			.address(address.getAddress())
			.addressDetail(address.getDetail())
			.desiredDeliveryDate(LocalDate.parse(createOrderRequest.getDesiredDeliveryDate(), formatter))
			.sender(createOrderRequest.getSender())
			.receiverContactNumber(createOrderRequest.getReceiverContactNumber())
			.senderContactNumber(createOrderRequest.getContactNumber())
			.orderEmail(createOrderRequest.getOrderEmail())
			.couponCode(createOrderRequest.getCouponCode())
			.deliveryRate(createOrderRequest.getDeliveryRate())
			.orderStatus(orderStatus)
			.deductedPoints(createOrderRequest.getDeductedPoints())
			.earnedPoints(createOrderRequest.getEarnedPoints())
			.deductedCouponPrice(createOrderRequest.getDeductedCouponPrice())
			.build();
	}
}
