package store.buzzbook.core.service.cart;

import store.buzzbook.core.dto.cart.CreateCartDetailRequest;
import store.buzzbook.core.dto.cart.GetCartResponse;
import store.buzzbook.core.dto.cart.UpdateCartRequest;

public interface CartService {
	GetCartResponse getCartByCartId(Long cartId);

	GetCartResponse createCart(Long userId);

	GetCartResponse getCartByUserId(Long userId);

	void createCartDetail(CreateCartDetailRequest createCartDetailRequest);

	void deleteCartDetail(Long cartDetailId);

	void deleteAll(Long cartId);

	void updateCartDetail(UpdateCartRequest updateCartRequest);
}
