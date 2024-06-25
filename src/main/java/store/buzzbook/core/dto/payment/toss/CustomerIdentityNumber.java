package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 명세서에 없는 객체
@Getter
@NoArgsConstructor
public class CustomerIdentityNumber {
	private CustomerMobilePhone customerMobilePhone;
	private String businessRegistrationNumber;
	private String cashReceiptCardNum;
}
