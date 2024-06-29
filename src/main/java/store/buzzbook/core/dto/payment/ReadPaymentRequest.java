package store.buzzbook.core.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadPaymentRequest {
	private String orderId;
	private String loginId;
}
