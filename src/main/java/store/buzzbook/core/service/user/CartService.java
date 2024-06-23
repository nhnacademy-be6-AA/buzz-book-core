package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.cart.GetCartResponse;

public interface CartService {
	GetCartResponse getCartByCartId(Long cartId);

	GetCartResponse createNewCart(Long userId);
}
