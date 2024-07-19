package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import store.buzzbook.core.dto.product.ProductDetailResponse;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.entity.order.OrderDetail;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.Publisher;
import store.buzzbook.core.entity.review.Review;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.review.ReviewRepository;
import store.buzzbook.core.service.review.ReviewService;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewService reviewService;

	@InjectMocks
	private ProductDetailService productDetailService;

	private Product testProduct;
	private Book testBook;
	private OrderDetail testOrderDetail;
	private Review testReview;
	private ReviewResponse testReviewResponse;
	private Page<Review> testReviewPage;

	@BeforeEach
	void setUp() {
		testProduct = Product.builder()
			.stock(10)
			.productName("test")
			.description("test")
			.price(1000)
			.forwardDate(LocalDate.now())
			.score(5)
			.thumbnailPath("test")
			.stockStatus(Product.StockStatus.SALE)
			.build();

		Publisher testPublisher = new Publisher("test");

		testBook = Book.builder()
			.title("test")
			.description("test")
			.isbn("test")
			.publisher(testPublisher)
			.publishDate("2024-01-01")
			.build();

		testOrderDetail = OrderDetail.builder()
			.id(1L)
			.price(1000)
			.build();

		testReview = new Review(
			1,
			"test",
			"test",
			5,
			LocalDateTime.now(),
			testOrderDetail
		);

		testReviewResponse = ReviewResponse.builder()
			.id(testReview.getId())
			.content(testReview.getContent())
			.reviewScore(testReview.getReviewScore())
			.orderDetailId(testOrderDetail.getId())
			.build();

		testReviewPage = new PageImpl<>(List.of(testReview));
	}

	@Test
	@DisplayName("convert product detail response")
	void convertProductDetailResponse() {
		// given
		when(bookRepository.findByProductId(anyInt())).thenReturn(testBook);
		when(reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(anyInt(),
			any())).thenReturn(testReviewPage);
		when(reviewService.constructorReviewResponse(any())).thenReturn(testReviewResponse);

		// when
		ProductDetailResponse productDetailResponse = productDetailService.convertProductDetailResponse(testProduct);

		// then
		assertTrue(Objects.nonNull(productDetailResponse));
		assert productDetailResponse.getBook() != null;
		assertEquals(productDetailResponse.getBook().getId(), testBook.getId());
		assertEquals(productDetailResponse.getReviews().size(), 1);
	}

	@Test
	@DisplayName("convert product detail response by id")
	void convertProductDetailResponseById() {
		// given
		when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
		when(bookRepository.findByProductId(anyInt())).thenReturn(testBook);
		when(reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(anyInt(),
			any())).thenReturn(testReviewPage);
		when(reviewService.constructorReviewResponse(any())).thenReturn(testReviewResponse);

		// when
		ProductDetailResponse productDetailResponse = productDetailService.convertProductDetailResponse(
			testProduct.getId());

		// then
		assertTrue(Objects.nonNull(productDetailResponse));
	}

	@Test
	@DisplayName("get product detail")
	void getProductDetail() {
		// given
		when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
		when(bookRepository.findByProductId(anyInt())).thenReturn(testBook);
		when(reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(anyInt(),
			any())).thenReturn(testReviewPage);
		when(reviewService.constructorReviewResponse(any())).thenReturn(testReviewResponse);

		// when
		ProductDetailResponse productDetailResponse = productDetailService.getProductDetail(testProduct.getId());

		// then
		assertTrue(Objects.nonNull(productDetailResponse));
	}
}
