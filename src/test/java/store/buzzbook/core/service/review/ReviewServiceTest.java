package store.buzzbook.core.service.review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static store.buzzbook.core.service.review.ReviewService.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.product.DataAlreadyException;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.review.ReviewRequest;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.point.PointPolicy;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.review.Review;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.order.OrderDetailRepository;
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.review.ReviewRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.image.ImageService;
import store.buzzbook.core.service.point.PointService;

@Slf4j

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
	@Mock
	User testUser;
	@Mock
	Product product;
	int productId = 1;
	@Mock
	Order order;
	@Mock
	OrderDetail orderDetail;
	long orderDetailId = 123L;
	// review
	int reviewId = 2;
	String testContent = "Great product!";
	int testScore = 5;
	LocalDateTime reviewCreateAt = LocalDateTime.now();
	// review Request
	ReviewRequest reviewRequest = new ReviewRequest(testContent, testScore, orderDetailId);
	// files
	@Mock
	MultipartFile multipartFile1;
	@Mock
	MultipartFile multipartFile2;
	@Mock
	PointPolicy pp;
	@Mock
	Review review1, review2, review3;
	@Mock
	OrderDetail orderDetail1, orderDetail2, orderDetail3;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PointPolicyRepository pointPolicyRepository;
	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private OrderDetailRepository orderDetailRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private PointService pointService;
	@Mock
	private ImageService imageClient;
	@InjectMocks
	private ReviewService reviewService;

	@Test
	void testSaveReview() {
		List<MultipartFile> imageFiles = List.of(multipartFile1, multipartFile2);

		when(orderDetail.getProduct()).thenReturn(product);
		when(product.getId()).thenReturn(productId);
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(order.getUser()).thenReturn(testUser);
		when(orderDetail.getOrder()).thenReturn(order);
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

		when(orderDetailRepository.findById(orderDetailId)).thenReturn(Optional.of(orderDetail));
		when(orderDetail.getId()).thenReturn(orderDetailId);
		when(reviewRepository.existsByOrderDetailId(orderDetailId)).thenReturn(false);
		when(imageClient.multiImageUpload(imageFiles)).thenReturn(List.of("path1", "path2"));
		when(pointPolicyRepository.findByName(anyString())).thenReturn(pp);

		ReviewResponse response = reviewService.saveReview(reviewRequest, imageFiles);

		assertNotNull(response);
		assertEquals(response.getContent(), testContent);
		assertEquals(response.getReviewScore(), testScore);
		assertEquals(response.getOrderDetailId(), orderDetailId);
		assertEquals(response.getProductId(), productId);
	}

	@Test
	@DisplayName("save review - no orderDetail")
	void testSaveReviewNoOrderDetail() {
		List<MultipartFile> imageFiles = List.of(multipartFile1, multipartFile2);
		when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(DataNotFoundException.class, () -> reviewService.saveReview(reviewRequest, imageFiles));
	}

	@Test
	@DisplayName("save review = already exist review")
	void testSaveReviewAlreadyExist() {
		List<MultipartFile> imageFiles = List.of(multipartFile1, multipartFile2);
		when(orderDetailRepository.findById(anyLong())).thenReturn(Optional.of(new OrderDetail()));
		when(reviewRepository.existsByOrderDetailId(anyLong())).thenReturn(true);

		assertThrows(DataAlreadyException.class, () -> reviewService.saveReview(reviewRequest, imageFiles));
	}

	@Test
	@DisplayName("getReview")
	void testGetReview() {
		Review testReview = new Review(2, testContent, null, testScore, reviewCreateAt, orderDetail);
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
		when(orderDetail.getOrder()).thenReturn(order);
		when(order.getUser()).thenReturn(testUser);
		when(testUser.getId()).thenReturn(1L);
		when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		when(orderDetail.getProduct()).thenReturn(product);
		when(product.getId()).thenReturn(productId);
		when(orderDetail.getId()).thenReturn(orderDetailId);

		ReviewResponse rr = reviewService.getReview(reviewId);

		assertNotNull(rr);
		assertEquals(rr.getContent(), testContent);
		assertEquals(rr.getReviewScore(), testScore);
		assertEquals(rr.getOrderDetailId(), orderDetailId);
		assertEquals(rr.getProductId(), productId);
	}

	@Test
	@DisplayName("no review")
	void testNoReview() {
		when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());
		assertThrows(DataNotFoundException.class, () -> reviewService.getReview(reviewId));
	}

	@Test
	@DisplayName("getReviewByOrderDetailId")
	void testGetReviewByOrderDetailId() {
		Review testReview = new Review(2, testContent, null, testScore, reviewCreateAt, orderDetail);
		when(reviewRepository.findByOrderDetailId(orderDetailId)).thenReturn(testReview);
		when(orderDetail.getOrder()).thenReturn(order);
		when(orderDetail.getProduct()).thenReturn(product);
		when(orderDetail.getId()).thenReturn(orderDetailId);
		when(product.getId()).thenReturn(productId);
		when(order.getUser()).thenReturn(testUser);
		when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		ReviewResponse rr = reviewService.getReviewByOrderDetailId(orderDetailId);

		assertNotNull(rr);
		assertEquals(rr.getContent(), testContent);
		assertEquals(rr.getReviewScore(), testScore);
		assertEquals(rr.getOrderDetailId(), orderDetailId);
		assertEquals(rr.getProductId(), productId);
	}

	@Test
	@DisplayName("no orderDetail")
	void testNoOrderDetail() {
		when(reviewRepository.findByOrderDetailId(orderDetailId)).thenReturn(null);
		ReviewResponse rr = reviewService.getReviewByOrderDetailId(orderDetailId);
		assertNull(rr);
	}

	@Test
	@DisplayName("findAllReviewByProductId")
	void testFindAllReviewByProductId() {
		int page = 0;
		int size = 10;
		List<Review> mockReviews = List.of(review1, review2, review3);

		Pageable pageable = PageRequest.of(page, size);
		Page<Review> mockPage = new PageImpl<>(mockReviews, pageable, mockReviews.size());
		when(reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(productId, pageable))
			.thenReturn(mockPage);

		List<Review> reviewList = List.of(review1, review2, review3);
		List<OrderDetail> orderDetailList = List.of(orderDetail1, orderDetail2, orderDetail3);

		for (int i = 0; i < 3; i++) {
			when(reviewList.get(i).getId()).thenReturn(i);
			when(reviewList.get(i).getOrderDetail()).thenReturn(orderDetailList.get(i));
			when(orderDetailList.get(i).getOrder()).thenReturn(order);
			when(orderDetailList.get(i).getProduct()).thenReturn(product);
			when(orderDetailList.get(i).getId()).thenReturn(orderDetailId + 1);
		}
		when(order.getUser()).thenReturn(testUser);
		when(testUser.getId()).thenReturn(1L);
		when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		when(product.getId()).thenReturn(productId);

		// When
		Page<ReviewResponse> resultPage = reviewService.findAllReviewByProductId(productId, page, size);

		// Then
		assertEquals(mockPage.getTotalElements(), resultPage.getTotalElements());
		assertEquals(mockPage.getNumber(), resultPage.getNumber());
		assertEquals(mockPage.getSize(), resultPage.getSize());
		assertEquals(mockPage.getContent().size(), resultPage.getContent().size());

		assertEquals(mockReviews.stream().map(reviewService::constructorReviewResponse).toList(),
			resultPage.getContent());

		verify(reviewRepository, times(1))
			.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(productId, pageable);
	}

	@Test
	@DisplayName("findAllReviewByUserId")
	void testFindAllReviewByUserId() {
		long userId = 1L;
		int page = 0;
		int size = 10;
		List<Review> mockReviews = List.of(review1, review2, review3);

		Pageable pageable = PageRequest.of(page, size);
		Page<Review> mockPage = new PageImpl<>(mockReviews, pageable, mockReviews.size());
		when(reviewRepository.findAllByOrderDetail_Order_User_IdOrderByReviewCreatedAtDesc(userId, pageable))
			.thenReturn(mockPage);

		List<Review> reviewList = List.of(review1, review2, review3);
		List<OrderDetail> orderDetailList = List.of(orderDetail1, orderDetail2, orderDetail3);

		for (int i = 0; i < 3; i++) {
			when(reviewList.get(i).getId()).thenReturn(i);
			when(reviewList.get(i).getOrderDetail()).thenReturn(orderDetailList.get(i));
			when(orderDetailList.get(i).getOrder()).thenReturn(order);
			when(orderDetailList.get(i).getProduct()).thenReturn(product);
			when(orderDetailList.get(i).getId()).thenReturn(orderDetailId + 1);
		}
		when(order.getUser()).thenReturn(testUser);
		when(testUser.getId()).thenReturn(1L);
		when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
		when(product.getId()).thenReturn(productId);

		// When
		Page<ReviewResponse> resultPage = reviewService.findAllReviewByUserId(userId, page, size);

		// Then
		assertEquals(mockPage.getTotalElements(), resultPage.getTotalElements());
		assertEquals(mockPage.getNumber(), resultPage.getNumber());
		assertEquals(mockPage.getSize(), resultPage.getSize());
		assertEquals(mockPage.getContent().size(), resultPage.getContent().size());

		assertEquals(mockReviews.stream().map(reviewService::constructorReviewResponse).toList(),
			resultPage.getContent());

		verify(reviewRepository, times(1))
			.findAllByOrderDetail_Order_User_IdOrderByReviewCreatedAtDesc(productId, pageable);
	}

	@Test
	@DisplayName("update review")
	void testUpdateReview() {
		LocalDateTime timestamp = LocalDateTime.now();
		Review updateTestReview = new Review(2, "리뷰 수정 내용", null, 10, timestamp, orderDetail1);
		ReviewRequest updateReq = new ReviewRequest("수정 할 내용", 1, 321L);
		when(orderDetail1.getId()).thenReturn(321L);
		when(orderDetail1.getProduct()).thenReturn(product);
		when(orderDetail1.getOrder()).thenReturn(order);
		when(order.getUser()).thenReturn(testUser);
		when(testUser.getId()).thenReturn(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(product.getId()).thenReturn(productId);
		when(productRepository.findById(productId)).thenReturn(Optional.of(product));
		when(reviewRepository.findById(anyInt())).thenReturn(Optional.of(updateTestReview));

		ReviewResponse rr = reviewService.updateReview(2, updateReq);

		assertEquals(updateTestReview.getId(), rr.getId());
		assertTrue(rr.getContent().contains(updateReq.getContent()));
		assertTrue(rr.getContent().contains(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	}

	@Test
	@DisplayName("update review - no review")
	void testUpdateReviewNoExistReview() {
		when(reviewRepository.findById(anyInt())).thenReturn(Optional.empty());
		assertThrows(DataNotFoundException.class, () -> reviewService.updateReview(1, reviewRequest));
	}

	@Test
	@DisplayName("constructorReviewResponse - with user data")
	void testConstructorReviewResponse() {
		User testUser1 = new User(123L, List.of(), "로그인 아이디", "연락처", "이름", "이메일주소", "비밀번호", LocalDate.now(),
			LocalDateTime.now(), LocalDateTime.now(), UserStatus.ACTIVE, LocalDateTime.now(), false);
		Review testReview = new Review(2, testContent, null, testScore, reviewCreateAt, orderDetail);

		when(orderDetail.getProduct()).thenReturn(product);
		when(product.getId()).thenReturn(productId);

		ReviewResponse rr = reviewService.constructorReviewResponse(testUser1, testReview);

		assertEquals(testReview.getId(), rr.getId());
		assertEquals(testReview.getContent(), rr.getContent());
		assertEquals(testReview.getReviewScore(), rr.getReviewScore());
		assertEquals(testReview.getReviewCreatedAt(), rr.getReviewCreatedAt());
		assertEquals(testUser1.getId(), rr.getUserId());
		assertEquals(testUser1.getName(), rr.getUserName());
		assertEquals(productId, rr.getProductId());
	}

	@Test
	@DisplayName("constructorReviewResponse - with image")
	void testConstructorReviewResponseWithImage() {
		String paths = "/test path1" + DELIMITER + "/test path2" + DELIMITER + "/test third path" + DELIMITER;

		List<String> pathList = List.of("/test path1", "/test path2", "/test third path");

		Review testReview = new Review(2, testContent, paths, testScore, reviewCreateAt, orderDetail);

		when(testUser.getId()).thenReturn(1L);
		when(testUser.getName()).thenReturn("test user");
		when(orderDetail.getProduct()).thenReturn(product);
		when(product.getId()).thenReturn(productId);

		ReviewResponse rr = reviewService.constructorReviewResponse(testUser, testReview);

		assertEquals(pathList.size(), Objects.requireNonNull(rr.getPicturePath()).size()); // 이미지 개수 확인

		for (int i = 0; i < pathList.size(); i++) {
			assertEquals(CLOUD_IMAGE_FILE_DEFAULT_PATH + pathList.get(i), rr.getPicturePath().get(i));	// 이미지 경로 확인
		}
	}

	@Test
	@DisplayName("constructorReviewResponse - only review data")
	void testConstructorReviewResponseOnlyReview() {
		Review testReview = new Review(2, testContent, null, testScore, reviewCreateAt, orderDetail);
		when(orderDetail.getOrder()).thenReturn(order);
		when(orderDetail.getProduct()).thenReturn(product);
		when(product.getId()).thenReturn(productId);
		when(order.getUser()).thenReturn(testUser);
		when(testUser.getId()).thenReturn(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

		ReviewResponse rr = reviewService.constructorReviewResponse(testReview);

		assertEquals(testReview.getId(), rr.getId());

	}

	@Test
	@DisplayName("constructorReviewResponse - only review data - not found user")
	void testConstructorReviewResponseNoUser() {
		Review testReview = new Review(2, testContent, null, testScore, reviewCreateAt, orderDetail);
		when(orderDetail.getOrder()).thenReturn(order);
		when(order.getUser()).thenReturn(testUser);
		when(testUser.getId()).thenReturn(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> reviewService.constructorReviewResponse(testReview));
	}

}

