package store.buzzbook.core.dto.payment.toss;

public class VirtualAccount {
	private String accountType;
	private String accountNumber;
	private String bankCode;
	private String customerName;
	private String dueDate;
	private String refundStatus;
	private boolean expired;
	private RefundReceiveAccount refundReceiveAccount;
}
