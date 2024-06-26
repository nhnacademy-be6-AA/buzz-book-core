package store.buzzbook.core.mapper.payment;

import java.time.format.DateTimeFormatter;

import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.entity.payment.BillLog;

public class BillLogMapper {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
}
