package store.buzzbook.core.mapper.payment;

import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.payment.BillLogResponse;
import store.buzzbook.core.entity.payment.BillLog;

public class BillLogMapper {
	public static BillLogResponse toDto(BillLog billLog, ReadOrderResponse readOrderResponse) {
		return BillLogResponse.builder()
			.id(billLog.getId())
			.price(billLog.getPrice())
			.readOrderResponse(readOrderResponse)
			.paymentDate(billLog.getPaymentDate())
			.paymentKey(billLog.getPaymentKey())
			.status(billLog.getStatus())
			.payment(billLog.getPayment())
			.build();
	}
}
