package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MobilePhone {
	private CustomerMobilePhone customerMobilePhone;
	private String settlementStatus;
	private String receiptUrl;
}
