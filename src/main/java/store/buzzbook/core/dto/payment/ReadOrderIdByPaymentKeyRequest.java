package store.buzzbook.core.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReadOrderIdByPaymentKeyRequest {
	private String paymentKey;
}
