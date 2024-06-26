package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Cancel {
	private Integer cancelAmount;
	private String cancelReason;
	private Integer taxFreeAmount;
	private Integer taxExemptionAmount;
	private Integer refundableAmount;
	private Integer easyPayDiscountAmount;
	private String canceledAt;
	private String transactionKey;
	private String receiptKey;
	private String cancelStatus;
	private String cancelrequestId;
}
