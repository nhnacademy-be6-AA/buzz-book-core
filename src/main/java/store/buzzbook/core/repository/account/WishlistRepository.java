package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.cart.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}
