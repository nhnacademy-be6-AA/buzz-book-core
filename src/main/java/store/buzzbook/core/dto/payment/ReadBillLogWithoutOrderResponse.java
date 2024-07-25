package store.buzzbook.core.dto.payment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.payment.BillStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReadBillLogWithoutOrderResponse {
	private Long id;
	private String payment;
	private Integer price;
	private LocalDateTime payAt;
	private BillStatus status;
	private String paymentKey;
	private String cancelReason;
}
