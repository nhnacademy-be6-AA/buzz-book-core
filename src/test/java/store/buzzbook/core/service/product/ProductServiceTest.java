package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.validation.ConstraintViolationException;
import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.ProductTagRepository;
import store.buzzbook.core.repository.product.TagRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private ProductTagRepository productTagRepository;

	@Mock
	private ProductSpecification productSpecification;

	@InjectMocks
	private ProductService productService;

	private Product product;
	private ProductRequest productRequest;
	private ProductUpdateRequest productUpdateRequest;

	@BeforeEach
	void setUp() {
		Category category = new Category(); // Category 객체 초기화
		product = Product.builder()
			.stock(100)
			.productName("Test Product")
			.description("This is a test product.")
			.price(50)
			.forwardDate(LocalDate.now())
			.score(5)
			.thumbnailPath("path/to/thumbnail")
			.stockStatus(Product.StockStatus.SALE)
			.category(category)
			.build();

	}

	@Test
	void testSaveProduct() {
		when(categoryRepository.findById(1)).thenReturn(Optional.of(new Category()));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
			Product savedProduct = invocation.getArgument(0);
			savedProduct.setId(1); // Simulate auto_increment id generation
			return savedProduct;
		});

		ProductResponse productResponse = productService.saveProduct(productRequest);

		assertNotNull(productResponse);
		assertEquals(1, productResponse.getId());
		assertEquals("Test Product", productResponse.getProductName());
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	void testSaveProduct_withInvalidRequest() {
		ProductRequest invalidRequest = new ProductRequest(
			100,
			"", // Invalid: blank product name
			"This is a test product.",
			-50, // Invalid: negative price
			"invalid-date", // Invalid: incorrect date format
			"path/to/thumbnail",
			Product.StockStatus.SALE,
			1,
			List.of("tag1", "tag2")
		);

		assertThrows(ConstraintViolationException.class, () -> productService.saveProduct(invalidRequest));
		verify(productRepository, times(0)).save(any(Product.class));
	}

	@Test
	void testGetAllProducts() {
		when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

		List<ProductResponse> productResponses = productService.getAllProducts();

		assertNotNull(productResponses);
		assertFalse(productResponses.isEmpty());
		assertEquals("Test Product", productResponses.get(0).getProductName());
		verify(productRepository, times(1)).findAll();
	}

	@Test
	void testGetProductById() {
		when(productRepository.findById(1)).thenReturn(Optional.of(product));

		ProductResponse productResponse = productService.getProductById(1);

		assertNotNull(productResponse);
		assertEquals("Test Product", productResponse.getProductName());
		verify(productRepository, times(1)).findById(1);
	}

	@Test
	void testUpdateProduct() {
		when(productRepository.findById(1)).thenReturn(Optional.of(product));
		when(categoryRepository.findById(1)).thenReturn(Optional.of(new Category()));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ProductResponse productResponse = productService.updateProduct(1, productUpdateRequest);

		assertNotNull(productResponse);
		assertEquals("Updated Product", productResponse.getProductName());
		assertEquals("Updated description", productResponse.getDescription());
		verify(productRepository, times(1)).findById(1);
		verify(productRepository, times(1)).save(any(Product.class));
		verify(productTagRepository, times(1)).deleteByProductId(1);
		verify(tagRepository, times(2)).findByName(anyString());
	}

	@Test
	void testDeleteProduct() {
		when(productRepository.findById(1)).thenReturn(Optional.of(product));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product deletedProduct = productService.deleteProduct(1);

		assertNotNull(deletedProduct);
		assertEquals(Product.StockStatus.SOLD_OUT, deletedProduct.getStockStatus());
		verify(productRepository, times(1)).findById(1);
		verify(productRepository, times(1)).save(any(Product.class));
	}

	@Test
	void testGetAllProductsByTitle() {
		when(productRepository.findAllByProductNameContaining("Test")).thenReturn(Collections.singletonList(product));

		List<ProductResponse> productResponses = productService.getAllProductsByTitle("Test");

		assertNotNull(productResponses);
		assertFalse(productResponses.isEmpty());
		assertEquals("Test Product", productResponses.get(0).getProductName());
		verify(productRepository, times(1)).findAllByProductNameContaining("Test");
	}

	@Test
	void testGetProductsByCriteria() {
		Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
		when(productRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(productPage);

		Page<ProductResponse> productResponses = productService.getProductsByCriteria(Product.StockStatus.SALE, "Test Product", 1, "reviews", 1, 10);

		assertNotNull(productResponses);
		assertFalse(productResponses.isEmpty());
		assertEquals("Test Product", productResponses.getContent().get(0).getProductName());
		verify(productRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
	}

	@Test
	void testGetLatestProducts() {
		when(productRepository.findProductsByLatestForwardDate(any(PageRequest.class))).thenReturn(Collections.singletonList(product));

		List<ProductResponse> productResponses = productService.getLatestProducts(1);

		assertNotNull(productResponses);
		assertFalse(productResponses.isEmpty());
		assertEquals("Test Product", productResponses.get(0).getProductName());
		verify(productRepository, times(1)).findProductsByLatestForwardDate(any(PageRequest.class));
	}
}