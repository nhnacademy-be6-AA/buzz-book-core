package store.buzzbook.core.service.cart;

import java.util.List;

import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;

public interface CartService {
	List<CartDetailResponse> getCartByCartId(Long cartId);

	Long createCart(Long userId);

	Long createCart();

	Long getCartIdByUserId(Long userId);

	List<CartDetailResponse> getCartByUserId(Long userId);

	void createCartDetail(Long cartId, CreateCartDetailRequest createCartDetailRequest);

	List<CartDetailResponse> deleteCartDetail(Long cartId, Long detailId);

	void deleteAll(Long cartId);

	void updateCartDetail(Long cartId, Long detailId, Integer quantity);
}
