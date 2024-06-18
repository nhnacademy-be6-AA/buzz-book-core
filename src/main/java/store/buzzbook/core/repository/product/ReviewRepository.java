package store.buzzbook.core.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.buzzbook.core.entity.product.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByProductId(Long productId);
}
