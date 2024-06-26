package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.cart.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
