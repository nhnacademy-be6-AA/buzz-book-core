package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;
import store.buzzbook.core.entity.product.Tag;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private ProductTagRepository productTagRepository;

	@InjectMocks
	private ProductService productService;

	@Test
	@DisplayName("상품 저장 테스트")
	void testSaveProduct() {
		// given
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
			.tags(List.of("새벽배송", "공식인증"))
			.build();

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

		// when
		ProductResponse productResponse = productService.saveProduct(request);

		// then
		assertNotNull(productResponse);
		assertEquals(request.getProductName(), productResponse.getProductName());
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	@DisplayName("상품 조회 테스트")
	void testGetProductById() {
		// given
		int productId = 1;
		Category category = new Category();
		Product product = Product.builder()
			.stock(10)
			.productName("갤럭시탭 S8")
			.description("감성대신 실력을 더했다")
			.price(87500)
			.forwardDate(LocalDate.now())
			.score(5)
			.thumbnailPath("path/to/thumbnail")
			.stockStatus(Product.StockStatus.SALE)
			.category(category)
			.build();

		when(productRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		ProductResponse productResponse = productService.getProductById(productId);

		// then
		assertNotNull(productResponse);
		assertEquals(product.getProductName(), productResponse.getProductName());
		verify(productRepository, times(1)).findById(productId);
	}

	@Test
	@DisplayName("상품 업데이트 테스트")
	void testUpdateProduct() {
		// given
		int productId = 1;
		int categoryId = 2;
		Category category = Category.builder()
			.id(categoryId)
			.name("Existing Category")
			.subCategories(new ArrayList<>()) // 빈 리스트로 초기화
			.build();

		Product existingProduct = Product.builder()
			.stock(10)
			.productName("갤럭시탭 S8")
			.description("감성대신 실력을 더했다")
			.price(87500)
			.forwardDate(LocalDate.now())
			.score(5)
			.thumbnailPath("path/to/thumbnail")
			.stockStatus(Product.StockStatus.SALE)
			.category(category)
			.build();

		ProductUpdateRequest updateRequest = ProductUpdateRequest.builder()
			.stock(15)
			.productName("갤럭시탭 S8 업데이트")
			.description("업데이트된 설명")
			.price(90000)
			.categoryId(categoryId)
			.stockStatus(Product.StockStatus.SOLD_OUT)
			.tags(List.of("새태그1", "새태그2"))
			.build();

		Category updatedCategory = Category.builder()
			.id(categoryId)
			.name("Updated Category")
			.subCategories(new ArrayList<>()) // 빈 리스트로 초기화
			.build();

		when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
		when(categoryRepository.findById(updateRequest.getCategoryId())).thenReturn(Optional.of(updatedCategory));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
		when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(productTagRepository.save(any(ProductTag.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		ProductResponse productResponse = productService.updateProduct(productId, updateRequest);

		// then
		assertNotNull(productResponse);
		assertEquals(updateRequest.getProductName(), productResponse.getProductName());
		assertEquals(updateRequest.getDescription(), productResponse.getDescription());
		assertEquals(updateRequest.getPrice(), productResponse.getPrice());
		assertEquals(existingProduct.getForwardDate(), productResponse.getForwardDate());
		assertEquals(existingProduct.getThumbnailPath(), productResponse.getThumbnailPath());
		assertEquals(updateRequest.getStockStatus(), productResponse.getStockStatus());
		assertEquals(updatedCategory.getId(), productResponse.getCategory().getId());

		verify(productRepository, times(1)).findById(productId);
		verify(categoryRepository, times(1)).findById(updateRequest.getCategoryId());
		verify(productRepository, times(1)).save(any(Product.class));
		verify(productTagRepository, times(1)).deleteByProductId(productId);
		verify(tagRepository, times(2)).findByName(anyString());
		verify(tagRepository, times(2)).save(any(Tag.class));
		verify(productTagRepository, times(2)).save(any(ProductTag.class));
	}
}