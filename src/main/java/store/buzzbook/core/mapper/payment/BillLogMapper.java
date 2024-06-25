package store.buzzbook.core.mapper.payment;

import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.entity.payment.BillLog;

public class BillLogMapper {
	public static ReadBillLogResponse toDto(BillLog billLog, ReadOrderResponse readOrderResponse) {
		return ReadBillLogResponse.builder()
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
