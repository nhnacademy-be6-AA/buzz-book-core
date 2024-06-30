package store.buzzbook.core.repository.user;

import java.util.List;
import java.util.Optional;

import store.buzzbook.core.dto.cart.CartDetailResponse;

public interface CartRepositoryCustom {
	Optional<List<CartDetailResponse>> findCartByCartId(Long cartId);

}
