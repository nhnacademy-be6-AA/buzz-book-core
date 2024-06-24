package store.buzzbook.core.repository.user;

import java.util.Optional;

import store.buzzbook.core.dto.cart.GetCartResponse;

public interface CartRepositoryCustom {
	Optional<GetCartResponse> findCartWithCartDetailList(Long cartId);
}
