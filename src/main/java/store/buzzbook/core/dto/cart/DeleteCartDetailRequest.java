package store.buzzbook.core.dto.cart;

import jakarta.validation.constraints.NotNull;

public record DeleteCartDetailRequest(
	@NotNull
	Long cartId,
	@NotNull
	Long productId
) {
}
