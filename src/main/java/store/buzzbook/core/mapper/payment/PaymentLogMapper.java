package store.buzzbook.core.mapper.payment;

import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.payment.CreatePaymentLogRequest;
import store.buzzbook.core.dto.payment.ReadPaymentLogResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.payment.PaymentLog;

public class PaymentLogMapper {
	public static ReadPaymentLogResponse toDto(PaymentLog paymentLog, ReadOrderResponse readOrderResponse) {
		return ReadPaymentLogResponse.builder()
			.id(paymentLog.getId())
			.name(paymentLog.getName())
			.price(paymentLog.getPrice())
			.readOrderResponse(readOrderResponse)
			.build();
	}

	public static PaymentLog toEntity(CreatePaymentLogRequest createPaymentLogRequest, Order order) {
		return PaymentLog.builder()
			.order(order)
			.price(createPaymentLogRequest.getAmount())
			.name(createPaymentLogRequest.getName())
			.build();
	}
}
