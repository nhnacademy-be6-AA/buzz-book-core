package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.cart.CartDetail;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
}
