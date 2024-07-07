package store.buzzbook.core.mapper.payment;

import java.time.LocalDateTime;

import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.payment.CreateBillLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.BillStatus;

public class BillLogMapper {

	public static ReadBillLogResponse toDto(BillLog billLog, ReadOrderResponse readOrderResponse) {
		return ReadBillLogResponse.builder()
			.id(billLog.getId())
			.price(billLog.getPrice())
			.readOrderResponse(readOrderResponse)
			.payAt(billLog.getPayAt())
			.paymentKey(billLog.getPaymentKey())
			.status(billLog.getStatus())
			.payment(billLog.getPayment())
			.cancelReason(billLog.getCancelReason())
			.build();
	}

	public static ReadBillLogWithoutOrderResponse toDtoWithoutOrder(BillLog billLog) {
		return ReadBillLogWithoutOrderResponse.builder()
			.id(billLog.getId())
			.price(billLog.getPrice())
			.payAt(billLog.getPayAt())
			.paymentKey(billLog.getPaymentKey())
			.status(billLog.getStatus())
			.payment(billLog.getPayment())
			.cancelReason(billLog.getCancelReason())
			.build();
	}

	public static BillLog toEntity(CreateBillLogRequest createBillLogRequest, Order order) {
		return BillLog.builder()
			.order(order)
			.payAt(LocalDateTime.now())
			.status(BillStatus.DONE)
			.paymentKey(createBillLogRequest.getPaymentKey())
			.payment(createBillLogRequest.getPayment())
			.price(createBillLogRequest.getPrice())
			.cancelReason(createBillLogRequest.getCancelReason())
			.build();
	}
}
