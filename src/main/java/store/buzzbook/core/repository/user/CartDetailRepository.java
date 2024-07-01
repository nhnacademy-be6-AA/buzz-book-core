package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
	void deleteAllByCart(Cart cart);
}
