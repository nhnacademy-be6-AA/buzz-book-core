package store.buzzbook.core.service.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import store.buzzbook.core.entity.product.Review;
import store.buzzbook.core.exception.product.ReviewNotFoundException;
import store.buzzbook.core.repository.product.ReviewRepository;

@Service
@Transactional
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	public Review save(Review review) {
		return reviewRepository.save(review);
	}

	public List<Review> findAllReviewsByProductId(Long productId) {
		return reviewRepository.findAllByProductId(productId);
	}

	public Review findReviewById(Long id) {
		return reviewRepository.findById(id)
			.orElseThrow(() -> new ReviewNotFoundException(id));
	}

	public Review updateReview(Review review) {
		if (!reviewRepository.existsById(review.getId())) {
			throw new ReviewNotFoundException(review.getId());
		}
		return reviewRepository.save(review);
	}
}
