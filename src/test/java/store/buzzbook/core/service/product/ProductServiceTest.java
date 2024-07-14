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
}