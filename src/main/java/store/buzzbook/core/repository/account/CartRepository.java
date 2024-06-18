package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.cart.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
