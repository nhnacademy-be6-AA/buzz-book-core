package store.buzzbook.core.service.product;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.dto.product.ProductDetailResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.review.Review;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.review.ReviewRepository;
import store.buzzbook.core.service.review.ReviewService;

@RequiredArgsConstructor
@Service
public class ProductDetailService {
	private final ProductRepository productRepository;
	private final BookRepository bookRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;

	@Transactional(readOnly = true)
	public ProductDetailResponse getProductDetail(int productId) {
		return convertProductDetailResponse(productId);
	}


	private ProductDetailResponse convertProductDetailResponse(Product product) {

		Book book = bookRepository.findByProductId(product.getId());
		List<Review> reviews = reviewRepository.findAllByOrderDetail_ProductIdOrderByReviewCreatedAtDesc(
			product.getId(), PageRequest.of(0, 5)).getContent();

		return ProductDetailResponse.builder()
			.book(BookResponse.convertToBookResponse(book))
			.reviews(reviews.stream().map(reviewService::constructorReviewResponse).toList())
			.build();
	}


	private ProductDetailResponse convertProductDetailResponse(int productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new DataNotFoundException("product", productId));
		return convertProductDetailResponse(product);
	}
}
