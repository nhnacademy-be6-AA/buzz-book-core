package store.buzzbook.core.repository.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.buzzbook.core.entity.review.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	Page<Review> findAllByOrderDetailProductId(long orderDetailId, Pageable pageable);

	List<Review> findAllByOrderDetail_ProductId(long orderDetailId);

	Page<Review> findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(long orderDetailId, Pageable pageable);

	//사용자가 가장 최근 작성한 리뷰부터 조회
	Page<Review> findAllByOrderDetail_Order_User_IdOrderByReviewCreatedAtDesc(long userId, Pageable pageable);
}
