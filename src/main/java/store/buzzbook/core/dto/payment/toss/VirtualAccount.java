package store.buzzbook.core.dto.payment.toss;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
