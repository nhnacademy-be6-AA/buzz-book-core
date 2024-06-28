package store.buzzbook.core.dto.payment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.payment.BillLog;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreatePaymentLogRequest {
	private long billLogId;
	private String name;
	private int amount;
	private String loginId;
	private String orderId;
}
