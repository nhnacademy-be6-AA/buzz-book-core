package store.buzzbook.core.mapper.payment;

import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.entity.payment.BillLog;

public class BillLogMapper {
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
