package store.buzzbook.core.service.review;

import static store.buzzbook.core.common.listener.OrderStatusListener.*;
import static store.buzzbook.core.common.listener.PointPolicyListener.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.product.DataAlreadyException;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.review.ReviewRequest;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.dto.review.OrderDetailsWithoutReviewResponse;
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
import store.buzzbook.core.service.image.ImageService;
import store.buzzbook.core.service.point.PointService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	static final String DELIMITER = " </> ";
	static final String CLOUD_IMAGE_FILE_DEFAULT_PATH = "http://image.toast.com/aaaacuf/aa-image/review";

	static final List<String> REVIEWABLE_STATUS_LIST = List.of(PAID, REFUND, PARTIAL_REFUND, SHIPPING_OUT, SHIPPED, BREAKAGE_REFUND);

	private final UserRepository userRepository;
	private final PointPolicyRepository pointPolicyRepository;
	private final ReviewRepository reviewRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ProductRepository productRepository;
	private final PointService pointService;
	private final ImageService imageClient;

	@Transactional
	public ReviewResponse saveReview(ReviewRequest reviewReq, List<MultipartFile> imageFiles) {

		// 상품상세조회확인
		OrderDetail orderDetail = orderDetailRepository.findById(reviewReq.getOrderDetailId())
			.orElseThrow(() -> new DataNotFoundException("orderDetail", reviewReq.getOrderDetailId()));

		// already review인지 확인
		if(reviewRepository.existsByOrderDetailId(reviewReq.getOrderDetailId())){
			throw new DataAlreadyException("이미 리뷰를 작성했습니다.");
		}

		// 리뷰저장
		String url = (imageFiles == null || imageFiles.isEmpty()) ? null : buildPathString(imageClient.multiImageUpload(imageFiles));
		Review review = new Review(reviewReq.getContent(), url, reviewReq.getReviewScore(), orderDetail);
		reviewRepository.save(review);

		// 상품점수에 리뷰반영
		updateProductScore(orderDetail.getProduct().getId());

		// 고객에 포인트 부여
		PointPolicy pp = (imageFiles == null || imageFiles.isEmpty()) ? pointPolicyRepository.findByName(REVIEW) :
			pointPolicyRepository.findByName(REVIEW_PHOTO);
		Order order = orderDetail.getOrder();
		User user = order.getUser();
		pointService.createPointLogWithDelta(user, pp.getName(), pp.getPoint());

		return constructorReviewResponse(review);
	}

	@Transactional(readOnly = true)
	public ReviewResponse getReview(int reviewId) {
		Review review = reviewRepository.findById(reviewId).orElse(null);
		if (review == null) {
			throw new DataNotFoundException("review", reviewId);
		}
		return constructorReviewResponse(review);
	}

	@Transactional(readOnly = true)
	public ReviewResponse getReviewByOrderDetailId(long orderDetailId) {
		Review review = reviewRepository.findByOrderDetailId(orderDetailId);
		if (review == null) {
			return null;
		}
		return constructorReviewResponse(review);
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponse> findAllReviewByProductId(int productId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(productId, pageable);
		return reviews.map(this::constructorReviewResponse);
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponse> findAllReviewByUserId(long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Review> reviews = reviewRepository.findAllByOrderDetail_Order_User_IdOrderByReviewCreatedAtDesc(userId,
			pageable);
		return reviews.map(this::constructorReviewResponse);
	}

	@Transactional
	public ReviewResponse updateReview(int reviewId, ReviewRequest reviewReq) {
		Review review = reviewRepository.findById(reviewId).orElse(null);
		if (review == null) {
			throw new DataNotFoundException("review", reviewId);
		}
		Review newReview = new Review(
			reviewId,
			reviewReq.getContent() + "\n(수정됨:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ")",
			review.getPicturePath(),
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

		List<String> picturePath = review.getPicturePath() == null ? List.of() : splitPathString(review.getPicturePath());

		return ReviewResponse.builder()
			.id(review.getId())
			.content(review.getContent())
			.picturePath(picturePath)
			.reviewScore(review.getReviewScore())
			.reviewCreatedAt(review.getReviewCreatedAt())
			.userId(user.getId())
			.orderDetailId(review.getOrderDetail().getId())
			.userName(user.getName())
			.productId(review.getOrderDetail().getProduct().getId())
			.build();
	}

	@Transactional(readOnly = true)
	public ReviewResponse constructorReviewResponse(Review review) {
		User user = userRepository.findById(review.getOrderDetail().getOrder().getUser().getId()).orElseThrow(
			UserNotFoundException::new);
		return constructorReviewResponse(user, review);
	}

	@Transactional(readOnly = true)
	public Page<OrderDetailsWithoutReviewResponse> findAllOrderDetailsByUserId(long userId, int page, int size){
		Pageable pageable = PageRequest.of(page, size);

		return orderDetailRepository.findAllNoExistReviewOrderDetailsByUserId(userId, REVIEWABLE_STATUS_LIST, pageable).map(
			OrderDetailsWithoutReviewResponse::new);
	}

	private String buildPathString(List<String> paths) {
		StringBuilder mergedPath = new StringBuilder();
		for (String path : paths) {
			if (path.startsWith(CLOUD_IMAGE_FILE_DEFAULT_PATH)) {
				mergedPath.append(path.substring(CLOUD_IMAGE_FILE_DEFAULT_PATH.length()));
			} else {
				mergedPath.append(path);
			}
			mergedPath.append(DELIMITER);
		}
		return mergedPath.toString();
	}

	private List<String> splitPathString(String pathString) {
		List<String> paths = new ArrayList<>();
		String[] parts = pathString.split(DELIMITER);

		for (String part : parts) {
			String trimmedPart = part.trim();
			if (!trimmedPart.isEmpty()) {
				paths.add(CLOUD_IMAGE_FILE_DEFAULT_PATH + trimmedPart);
			}
		}
		return paths;
	}
}
