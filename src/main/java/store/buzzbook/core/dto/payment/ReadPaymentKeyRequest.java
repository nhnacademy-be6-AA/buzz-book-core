package store.buzzbook.core.dto.payment;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReadPaymentKeyRequest {
	@NotNull
	private String orderId;
	@Nullable
	private String orderPassword;
}
