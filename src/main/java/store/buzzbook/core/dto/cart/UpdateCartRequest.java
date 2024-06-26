package store.buzzbook.core.dto.cart;

import jakarta.validation.constraints.NotNull;

public record UpdateCartRequest(
	@NotNull
	long id,
	@NotNull
	int quantity
) {
}
