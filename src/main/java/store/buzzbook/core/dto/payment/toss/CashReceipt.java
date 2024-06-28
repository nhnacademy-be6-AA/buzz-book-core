package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CashReceipt {
	private String type;
	private String receiptKey;
	private String issueNumber;
	private String receiptUrl;
	private Integer amount;
	private Integer taxFreeAmount;

	private String orderId;
	private String orderName;
	private String busimessNumber;
	private String transactionType;
	private String issueStatus;
	private Failure failure;
	private String customerIdentityNumber;
	private String requestedAt;
}
