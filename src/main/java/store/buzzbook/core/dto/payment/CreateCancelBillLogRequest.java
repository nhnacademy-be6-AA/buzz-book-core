package store.buzzbook.core.dto.payment;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.payment.BillStatus;

@Getter
@NoArgsConstructor
public class CreateCancelBillLogRequest {
	private String paymentKey;
	@Nullable
	private String cancelReason;
	private BillStatus status;
}
