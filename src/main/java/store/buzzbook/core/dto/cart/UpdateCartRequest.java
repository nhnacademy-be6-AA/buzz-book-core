package store.buzzbook.core.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCartRequest(
	@NotNull
	long id,
	@NotNull
	int quantity,
	@NotNull
	long cartId
) {
}
