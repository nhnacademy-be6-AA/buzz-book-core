package store.buzzbook.core.service.review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.review.ReviewCreateRequest;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.dto.review.ReviewUpdateRequest;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.point.PointPolicy;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.review.Review;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.review.ReviewRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.point.PointService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final UserRepository userRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final ReviewRepository reviewRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ProductRepository productRepository;
	private final PointService pointService;

	public ReviewResponse saveReview(ReviewCreateRequest reviewReq) {

		//리뷰
		OrderDetail orderDetail = orderDetailRepository.findById(reviewReq.getOrderDetailId()).orElse(null);
		if (orderDetail == null) {
			throw new DataNotFoundException("orderDetail", reviewReq.getOrderDetailId());
		}
		Review review = new Review(reviewReq.getContent(), reviewReq.getPicturePath(), reviewReq.getReviewScore(),
			orderDetail);

		reviewRepository.save(review);

		//리뷰 점수로 상품 점수 수정
		updateProductScore(review.getOrderDetail().getProduct().getId());

		//리뷰단 고객 포인트 부여
		Order order = review.getOrderDetail().getOrder();
		User user = order.getUser();
		PointPolicy pp;
		if (review.getPicturePath() == null) {
			pp = pointPolicyRepository.findByName("리뷰 작성");
		} else {
			pp = pointPolicyRepository.findByName("사진 리뷰 작성");
		}
		pointService.createPointLogWithDelta(user, pp.getName(), pp.getPoint());

		return constructorReviewResponse(review);
	}

	public ReviewResponse getReview(int reviewId) {
		Review review = reviewRepository.findById(reviewId).orElse(null);
		if (review == null) {
			throw new DataNotFoundException("review", reviewId);
		}
		return constructorReviewResponse(review);
	}

	public Page<ReviewResponse> findAllReviewByProductId(int productId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(productId, pageable);
		return reviews.map(this::constructorReviewResponse);
	}

	public Page<ReviewResponse> findAllReviewByUserId(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderDetail_Order_User_IdOrderByReviewCreatedAtDesc(userId,
			pageable);
		return reviews.map(this::constructorReviewResponse);
	}

	public ReviewResponse updateReview(ReviewUpdateRequest reviewReq) {
		Review review = reviewRepository.findById(reviewReq.getId()).orElse(null);
		if (review == null) {
			throw new DataNotFoundException("review", reviewReq.getId());
		}
		Review newReview = new Review(
			reviewReq.getId(),
			reviewReq.getContent() + "\n(수정됨:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ")",
			reviewReq.getPicturePath(),
			reviewReq.getReviewScore(),
			review.getReviewCreatedAt(),
			review.getOrderDetail());

		reviewRepository.save(newReview);

		//리뷰 점수로 상품 점수 수정
		updateProductScore(review.getOrderDetail().getProduct().getId());

		return constructorReviewResponse(newReview);

	}

	private void updateProductScore(int productId) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			throw new DataNotFoundException("product", productId);
		}
		List<Review> productReviews = reviewRepository.findAllByOrderDetail_ProductId(product.getId());
		double productScore = productReviews.stream().mapToDouble(Review::getReviewScore).sum() / productReviews.size();
		product.setScore((int)Math.round(productScore));
		productRepository.save(product);
	}

	public ReviewResponse constructorReviewResponse(User user, Review review) {
		return ReviewResponse.builder()
			.id(review.getId())
			.content(review.getContent())
			.picturePath(review.getPicturePath())
			.reviewScore(review.getReviewScore())
			.reviewCreatedAt(review.getReviewCreatedAt())
			.userId(user.getId())
			.orderDetail(review.getOrderDetail().getId())
			.userName(user.getName())
			.build();
	}

	public ReviewResponse constructorReviewResponse(Review review) {
		User user = userRepository.findById(review.getOrderDetail().getOrder().getUser().getId()).orElseThrow(
			UserNotFoundException::new);
		return constructorReviewResponse(user, review);
	}
}
