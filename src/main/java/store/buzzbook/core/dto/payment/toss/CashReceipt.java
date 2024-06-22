package store.buzzbook.core.dto.payment.toss;

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
	private CustomerIdentityNumber customerIdentityNumber;
	private String requestedAt;
}
