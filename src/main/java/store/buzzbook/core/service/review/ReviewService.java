package store.buzzbook.core.service.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.review.ReviewCreateRequest;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.dto.review.ReviewUpdateRequest;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.review.Review;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.review.ReviewRepository;
import store.buzzbook.core.service.point.PointService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private static final int REVIEW_POINT = 200;
	private static final int REVIEW_WITH_PICTURE_POINT = 500;
	private static final String REVIEW_POINT_MESSAGE = "리뷰 등록";
	private static final String REVIEW_WITH_PICTURE_POINT_MESSAGE = "사진 리뷰 등록";

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

		log.info("{}", review);

		//리뷰 점수로 상품 점수 수정
		Product product = productRepository.findById(review.getOrderDetail().getProduct().getId()).orElse(null);
		List<Review> productReviews = reviewRepository.findAllByOrderDetail_ProductId(product.getId());
		double productScore = productReviews.stream().mapToDouble(Review::getReviewScore).sum() / productReviews.size();
		product.setScore((int)Math.round(productScore));


		//리뷰단 고객 포인트 부여
		Order order = review.getOrderDetail().getOrder();
		User user = order.getUser();
		if (review.getPicturePath() == null) {
			pointService.createPointLogWithDelta(user, REVIEW_POINT_MESSAGE, REVIEW_POINT);
		} else {
			pointService.createPointLogWithDelta(user, REVIEW_WITH_PICTURE_POINT_MESSAGE, REVIEW_WITH_PICTURE_POINT);
		}


		reviewRepository.save(review);
		productRepository.save(product);

		ReviewResponse rr = new ReviewResponse(review);

		log.info("{}", rr);

		return rr;
	}

	public ReviewResponse getReview(int reviewId) {
		Review review = reviewRepository.findById(reviewId).orElse(null);
		if (review == null) {
			throw new DataNotFoundException("review", reviewId);
		}
		return new ReviewResponse(review);
	}

	public Page<ReviewResponse> findAllReviewByProductId(Long productId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderDetailProductId(productId, pageable);
		return reviews.map(ReviewResponse::new);
	}

	public Page<ReviewResponse> findAllReviewByUserId(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderDetailOrderUser_IdOrderByReviewCreatedAtDesc(userId, pageable);
		return reviews.map(ReviewResponse::new);
	}

	public ReviewResponse updateReview(ReviewUpdateRequest reviewReq) {
		Review review = reviewRepository.findById(reviewReq.getId()).orElse(null);
		if (review == null) {
			throw new DataNotFoundException("review", reviewReq.getId());
		}
		return new ReviewResponse(reviewRepository.save(review));
	}
}
