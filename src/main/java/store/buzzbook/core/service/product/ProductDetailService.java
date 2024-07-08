// package store.buzzbook.core.service.product;
//
// import org.springframework.stereotype.Service;
//
// import lombok.RequiredArgsConstructor;
// import store.buzzbook.core.dto.product.BookResponse;
// import store.buzzbook.core.dto.product.ProductDetailResponse;
// import store.buzzbook.core.dto.product.ProductResponse;
// import store.buzzbook.core.entity.product.Product;
// import store.buzzbook.core.repository.product.BookRepository;
// import store.buzzbook.core.repository.product.ProductRepository;
// import store.buzzbook.core.service.review.ReviewService;
//
// @RequiredArgsConstructor
// @Service
// public class ProductDetailService {
// 	private final ProductRepository productRepository;
// 	private final BookRepository bookRepository;
// 	private final ReviewService reviewService;
//
// 	public ProductDetailResponse convertProductDetailResponse(Product product){
//
// 		Object[] obj = productRepository.findRecentReviewsForProduct(product.getId());
//
// 		return ProductDetailResponse.builder()
// 			.product(ProductResponse.convertToProductResponse((Product)obj[0]))
// 			.book(BookResponse.convertToBookResponse(bookRepository.findByProductId(product.getId())))
// 			.reviews(reviewService.findAllReviewByProductId(product.getId(), 0, 5))
// 			.build();
// 	}
// }
