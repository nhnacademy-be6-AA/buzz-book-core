package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefundReceiveAccount {
	private String bankCode;
	private String accountNumber;
	private String holderName;
}
