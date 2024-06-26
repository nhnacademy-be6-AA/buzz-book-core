package store.buzzbook.core.dto.payment;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.entity.payment.BillStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReadBillLogResponse {
	private long id;

	private String payment;
	private int price;
	private LocalDate paymentDate;

	private BillStatus status;

	private UUID paymentKey;
	private ReadOrderResponse readOrderResponse;
	private String cancelReason;
}
