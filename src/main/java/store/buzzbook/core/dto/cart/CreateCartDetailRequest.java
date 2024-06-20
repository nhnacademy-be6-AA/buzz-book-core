package store.buzzbook.core.dto.cart;


public record CreateCartDetailRequest(Long userId,
									  Long cartId,
									  Integer quantity,
									  Long productId) {
}
