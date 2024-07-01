package store.buzzbook.core.service.cart;

import java.util.List;

import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.cart.CreateCartDetailRequest;

public interface CartService {
	List<CartDetailResponse> getCartByUuId(String uuid);

	String createCart(Long userId);

	String createCart();

	String getUuidByUserId(Long userId);

	List<CartDetailResponse> getCartByUserId(Long userId);

	void createCartDetail(String uuid, CreateCartDetailRequest createCartDetailRequest);

	List<CartDetailResponse> deleteCartDetail(String uuid, Long detailId);

	void deleteAll(String uuid);

	void updateCartDetail(Long detailId, Integer quantity);

	boolean isValidUUID(String uuid);
}
