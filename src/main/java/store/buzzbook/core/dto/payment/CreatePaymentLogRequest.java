package store.buzzbook.core.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.payment.BillLog;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreatePaymentLogRequest {
	private long billLogId;

	private String name;
	private int price;
}
