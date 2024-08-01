package store.buzzbook.core.service.product;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private ProductSpecification productSpecification;

	@Test
	void testOrderBy_name() {
		// Given
		String orderBy = "name";
		Root<Product> root = mock(Root.class);
		CriteriaQuery<?> query = mock(CriteriaQuery.class);
		CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

		// When
		productSpecification.orderBy(orderBy).toPredicate(root, query, criteriaBuilder);

		// Then
		verify(criteriaBuilder).asc(root.get("productName"));
	}

	@Test
	void testOrderBy_score() {
		// Given
		String orderBy = "score";
		Root<Product> root = mock(Root.class);
		CriteriaQuery<?> query = mock(CriteriaQuery.class);
		CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

		// When
		productSpecification.orderBy(orderBy).toPredicate(root, query, criteriaBuilder);

		// Then
		verify(criteriaBuilder).desc(root.get("score"));
	}

	@Test
	void testOrderBy_reviews() {
		// Given
		String orderBy = "reviews";
		Root<Product> root = mock(Root.class);
		CriteriaQuery<?> query = mock(CriteriaQuery.class);
		CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

		// When
		productSpecification.orderBy(orderBy).toPredicate(root, query, criteriaBuilder);

		// Then
		verify(criteriaBuilder).desc(root.get("reviews"));
	}

	@Test
	void testOrderBy_default() {
		// Given
		String orderBy = "unknown";
		Root<Product> root = mock(Root.class);
		CriteriaQuery<?> query = mock(CriteriaQuery.class);
		CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

		// When
		productSpecification.orderBy(orderBy).toPredicate(root, query, criteriaBuilder);

		// Then
		verify(criteriaBuilder).asc(root.get("id"));
	}

	@Mock
	Category category;

	// @Disabled
	// @Test
	// void testGetProductsByCriteria() {
	// 	// Given
	// 	Product.StockStatus stockStatus = Product.StockStatus.SALE;
	// 	String name = "test";
	// 	Integer categoryId = 1;
	//
	// 	Root<Product> root = mock(Root.class);
	// 	CriteriaQuery<?> query = mock(CriteriaQuery.class);
	// 	CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
	//
	// 	// Mock CategoryRepository behavior
	// 	when(category.getId()).thenReturn(1);
	// 	when(categoryRepository.findById(anyInt())).thenReturn(java.util.Optional.of(category));
	//
	// 	// When
	// 	productSpecification.getProductsByCriteria(stockStatus, name, categoryId).toPredicate(root, query, criteriaBuilder);
	//
	// 	// Then
	// 	verify(criteriaBuilder).equal(root.get("stockStatus"), stockStatus);
	// 	verify(criteriaBuilder).like(root.get("productName"), "%" + name + "%");
	// 	verify(root.get("category").get("id")).in(anyList());
	// }

	// @Disabled
	// @Test
	// void testGetProductsByCriteria_NullParameters() {
	// 	// Given
	// 	Root<Product> root = mock(Root.class);
	// 	CriteriaQuery<?> query = mock(CriteriaQuery.class);
	// 	CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
	//
	// 	// When
	// 	productSpecification.getProductsByCriteria(null, null, null).toPredicate(root, query, criteriaBuilder);
	//
	// 	// Then
	// 	verify(criteriaBuilder, never()).equal(any(), any());
	// }
}
