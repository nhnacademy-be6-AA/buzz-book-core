package store.buzzbook.core.controller.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.buzzbook.core.entity.product.Review;
import store.buzzbook.core.service.product.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Review> getReview(@PathVariable Long id) {
		Review review = reviewService.findReviewById(id);
		return ResponseEntity.ok(review);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
		List<Review> reviews = reviewService.findAllReviewsByProductId(productId);
		return ResponseEntity.ok(reviews);
	}

	@PostMapping
	public ResponseEntity<Review> addReview(@RequestBody Review review) {
		Review savedReview = reviewService.save(review);
		return ResponseEntity.ok(savedReview);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review) {
		Review updatedReview = reviewService.updateReview(review);
		return ResponseEntity.ok(updatedReview);
	}
}
