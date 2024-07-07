package store.buzzbook.core.dto.payment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.buzzbook.core.dto.order.ReadOrderResponse;
import store.buzzbook.core.entity.payment.BillStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class ReadBillLogResponse {
	private long id;

	private String payment;
	private int price;
	private LocalDateTime payAt;

	private BillStatus status;

	private String paymentKey;

	private ReadOrderResponse readOrderResponse;

	@Nullable
	private String cancelReason;
}
