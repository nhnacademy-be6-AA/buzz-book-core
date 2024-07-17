package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@SpringBootTest
@RequiredArgsConstructor
class ProductServiceTest {

	@MockBean
	private ProductRepository productRepository;

	@MockBean
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductService productService;

	@BeforeEach
	void setUp() {
		//임시
	}

	@Test
	@DisplayName("상품 저장 테스트")
	void testSaveProduct() {
		//given


		Category category = new Category();
		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		ProductRequest request = ProductRequest.builder()
			.stock(5)
			.productName("갤럭시탭 S8")
			.description("감성대신 실력을 더했다 내장 4092 픽셀의 정규화된 펜 촉")
			.price(87500)
			.forwardDate("2024-07-01")
			.thumbnailPath("path/to/thumbnail")
			.stockStatus(Product.StockStatus.SALE)
			.categoryId(1)
			.tags(List.of("새벽배송","공식인증"))
			.build();

		Category category1 = new Category();
		when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

		Product product = Product.builder()
			.stock(request.getStock())
			.productName(request.getProductName())
			.description(request.getDescription())
			.price(request.getPrice())
			.forwardDate(LocalDate.parse(request.getForwardDate()))
			.thumbnailPath(request.getThumbnailPath())
			.stockStatus(request.getStockStatus())
			.category(category)
			.build();

		when(productRepository.save(any(Product.class))).thenReturn(product);

		//when
		ProductResponse productResponse = productService.saveProduct(request);

		//then
		assertNotNull(productResponse);
		assertEquals(request.getProductName(), productResponse.getProductName());
		verify(productRepository, times(1)).save(any(Product.class));
	}

	// @Test
	// @DisplayName("상품 전부 가져오기 테스트")
	// void testGetAllProduct() {
	// 	//given
	// 	List<Product> products = List.of(new Product(), new Product());
	// 	when(productRepository.findAll()).thenReturn(products);
	//
	// 	//when
	// 	List<ProductResponse> productResponses = productService.getAllProducts();
	//
	// 	//then
	// 	assertEquals(2, productResponses.size());
	// 	verify(productRepository, times(1)).findAll();
	// }

	// @Test
	// @DisplayName("상품 업데이트 테스트")
	// void testUpdateProduct() {
	// 	// given
	// 	Product existingProduct = Product.builder()
	//
	// 		.stock(10)
	// 		.productName("Old Product")
	// 		.description("Old Description")
	// 		.price(50000)
	// 		.forwardDate(LocalDate.parse("2023-01-01"))
	// 		.thumbnailPath("path/to/old-thumbnail")
	// 		.stockStatus(Product.StockStatus.SALE)
	// 		.category(new Category())
	// 		.build();
	//
	// 	when(productRepository.findById(1)).thenReturn(Optional.of(existingProduct));
	//
	// 	ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
	// 		.stock(15)
	// 		.productName("Updated Product")
	// 		.description("Updated Description")
	// 		.price(75000)
	// 		.stockStatus(Product.StockStatus.SOLD_OUT)
	// 		.categoryId(1)
	// 		.tags(List.of("UpdatedTag1", "UpdatedTag2"))
	// 		.build();
	//
	// 	Category category = new Category();
	// 	when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
	//
	// 	Product updatedProduct = Product.builder()
	// 		.stock(updateRequest.getStock())
	// 		.productName(updateRequest.getProductName())
	// 		.description(updateRequest.getDescription())
	// 		.price(updateRequest.getPrice())
	// 		.forwardDate(existingProduct.getForwardDate())  // ForwardDate는 그대로 유지
	// 		.thumbnailPath(existingProduct.getThumbnailPath())  // ThumbnailPath는 그대로 유지
	// 		.stockStatus(updateRequest.getStockStatus())
	// 		.category(category)
	// 		.build();
	//
	// 	when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
	//
	// 	// when
	// 	ProductResponse productResponse = productService.updateProduct(productId, updateRequest);
	//
	// 	// then
	// 	assertNotNull(productResponse);
	// 	assertEquals("Updated Product", productResponse.getProductName());
	// 	assertEquals(productId, productResponse.getId());
	// 	verify(productRepository, times(1)).findById(productId);
	// 	verify(categoryRepository, times(1)).findById(1);
	// 	verify(productRepository, times(1)).save(any(Product.class));
	// }
}