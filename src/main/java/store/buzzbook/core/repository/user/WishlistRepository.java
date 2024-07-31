package store.buzzbook.core.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.cart.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
	Page<Wishlist> findAllByUserId(long userId, Pageable pageable);
	Boolean existsByUserIdAndProductId(long userId, int productId);
}
