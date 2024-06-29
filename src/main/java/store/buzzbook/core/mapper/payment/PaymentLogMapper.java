package store.buzzbook.core.mapper.payment;

import store.buzzbook.core.dto.payment.CreatePaymentLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadPaymentLogResponse;
import store.buzzbook.core.entity.payment.BillLog;
import store.buzzbook.core.entity.payment.PaymentLog;

public class PaymentLogMapper {

	public static ReadPaymentLogResponse toDto(PaymentLog paymentLog, ReadBillLogResponse readBillLogResponse) {
		return ReadPaymentLogResponse.builder()
			.id(paymentLog.getId())
			.name(paymentLog.getName())
			.price(paymentLog.getPrice())
			.readBillLogResponse(readBillLogResponse)
			.build();
	}

	public static PaymentLog toEntity(CreatePaymentLogRequest createPaymentLogRequest, BillLog billLog) {
		return PaymentLog.builder()
			.billLog(billLog)
			.price(createPaymentLogRequest.getAmount())
			.name(createPaymentLogRequest.getName())
			.build();
	}
}
