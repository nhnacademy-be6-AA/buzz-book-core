package store.buzzbook.core.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateBillLogRequest {
	private String payment;
	private int price;
	private String paymentKey;
	private String orderId;
	private String cancelReason;
}
