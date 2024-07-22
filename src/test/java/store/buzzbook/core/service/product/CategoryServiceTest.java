package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.common.exception.product.DataAlreadyException;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.CategoryRequest;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private CategoryService categoryService;

	private CategoryRequest categoryRequest;
	private Category parentCategory;
	private Category subCategory1;
	private Category subCategory2;
	private Category newCategory;

	@BeforeEach
	void setUp() {
		// Mock parent and sub categories
		parentCategory = new Category(1, "Parent Category", null, new ArrayList<>());
		subCategory1 = new Category(3, "Subcategory 1", parentCategory, new ArrayList<>());
		subCategory2 = new Category(4, "Subcategory 2", parentCategory, new ArrayList<>());

		// Mock CategoryRequest
		categoryRequest = new CategoryRequest();
		categoryRequest.setName("New Category");
		categoryRequest.setParentCategoryId(1); // Parent category id
		categoryRequest.setSubCategoryIds(List.of(3, 4)); // Subcategory ids

		newCategory = Category.builder()
			.id(2)
			.name(categoryRequest.getName())
			.parentCategory(parentCategory)
			.subCategories(List.of(subCategory1, subCategory2))
			.build();
	}

	@Test
	@DisplayName("Create Category")
	void testCreateCategory() {

		when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(parentCategory));
		when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

		// When
		CategoryResponse response = categoryService.createCategory(categoryRequest);

		// Then
		verify(categoryRepository, times(1)).findById(anyInt());
		verify(categoryRepository, times(1)).save(any(Category.class));

		assertNotNull(response);
		assertEquals("New Category", response.getName());
		assertEquals(2, response.getId()); // Assert the mocked id value
	}

	@Test
	@DisplayName("Create Category - no exist parentCategory")
	void testCreateCategoryNoParentCategory() {
		when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());
		assertThrows(DataNotFoundException.class, () -> categoryService.createCategory(categoryRequest));
	}


	@Test
	@DisplayName("Update Category")
	void testUpdateCategory() {

		when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(parentCategory));
		when(categoryRepository.findAllByIdIn(anyList())).thenReturn(List.of(subCategory1, subCategory2));
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		CategoryResponse response = categoryService.updateCategory(1, categoryRequest);

		// Then

		verify(categoryRepository, times(1)).save(any(Category.class));

		assertEquals(parentCategory.getId(), response.getParentCategory().keySet().toArray()[0]);
		assertEquals(parentCategory.getName(), response.getParentCategory().values().toArray()[0]);
		assertEquals(2, response.getSubCategory().size());
		assertEquals(subCategory1.getId(), response.getSubCategory().keySet().toArray()[0]);
		assertEquals(subCategory1.getName(), response.getSubCategory().values().toArray()[0]);
		assertEquals(subCategory2.getId(), response.getSubCategory().keySet().toArray()[1]);
		assertEquals(subCategory2.getName(), response.getSubCategory().values().toArray()[1]);
	}

	@Test
	@DisplayName("updateCategory - NotFoundCategory")
	void testUpdateCategoryNotFound() {

		int noExistingCategoryId = -1;

		when(categoryRepository.findById(noExistingCategoryId)).thenReturn(Optional.empty());
		assertThrows(DataNotFoundException.class, () -> categoryService.updateCategory(noExistingCategoryId, categoryRequest));
	}


	@Test
	@DisplayName("updateCategory - NotMatchSubCategory")
	void testUpdateCategoryNotMatchSubCategory() {

		int categoryId = 2;

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(newCategory));
		when(categoryRepository.findAllByIdIn(anyList())).thenReturn(List.of(new Category()));

		assertThrows(DataNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));
	}

	@Test
	@DisplayName("updateCategory - NotFoundParentCategory")
	void testUpdateCategoryNotFoundParentCategory() {

		int categoryId = 2;
		int parentCategoryId = 1;

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(newCategory));
		when(categoryRepository.findAllByIdIn(anyList())).thenReturn(List.of(subCategory1, subCategory2));
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.empty());

		assertThrows(DataNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));
	}

	@Test
	@DisplayName("Delete Category")
	void testDeleteCategory() {

		when(categoryRepository.existsById(1)).thenReturn(true);
		when(productRepository.findByCategoryId(1)).thenReturn(new ArrayList<>());

		// Execute the service method
		assertDoesNotThrow(() -> categoryService.deleteCategory(1));

		// Verify repository interactions
		verify(categoryRepository, times(1)).existsById(1);
		verify(productRepository, times(1)).findByCategoryId(1);
		verify(categoryRepository, times(1)).deleteById(1);
	}

	@Test
	@DisplayName("Get Top Categories")
	void testGetTopCategories() {
		// Mock repository behavior
		List<Category> topCategories = List.of(parentCategory);
		when(categoryRepository.findAllByParentCategoryIsNull()).thenReturn(topCategories);

		// Execute the service method
		CategoryResponse response = categoryService.getTopCategories();

		// Verify repository interactions
		verify(categoryRepository, times(1)).findAllByParentCategoryIsNull();

		// Assertions
		assertNotNull(response);
		assertEquals("Parent Category", response.getName());
	}

	@Test
	@DisplayName("Top Category is 2")
	void testTopCategoryIs2() {
		when(categoryRepository.findAllByParentCategoryIsNull()).thenReturn(List.of(new Category(), new Category()));

		assertThrows(DataAlreadyException.class, () -> categoryService.getTopCategories());
	}

	@Test
	@DisplayName("Get Sub Categories Response")
	void testGetSubCategoriesResponse() {
		// Mock repository behavior
		when(categoryRepository.findById(1)).thenReturn(Optional.of(parentCategory));

		// Execute the service method
		CategoryResponse response = categoryService.getSubCategoriesResponse(1);

		// Verify repository interactions
		verify(categoryRepository, times(1)).findById(1);

		// Assertions
		assertNotNull(response);
		assertEquals("Parent Category", response.getName());
	}

	@Test
	@DisplayName("getSubCategoriesResponse - no exist category")
	void testGetSubCategoriesResponseNoExistCategory() {

		int noExistCategoryId = 999;

		when(categoryRepository.findById(noExistCategoryId)).thenReturn(Optional.empty());
		assertThrows(DataNotFoundException.class, () ->
			categoryService.getSubCategoriesResponse(noExistCategoryId));
	}


	@Test
	@DisplayName("Get pageable Category Responses")
	void testGetPageableCategoryResponses() {

		List<Category> categoryList = List.of(parentCategory, newCategory, subCategory1, subCategory2);
		Page<Category> categoryPage = new PageImpl<>(categoryList);
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);
		when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

		// When
		Page<CategoryResponse> responsePage = categoryService.getPageableCategoryResponses(page, size);

		// Then
		assertNotNull(responsePage);
		assertEquals(categoryPage.getTotalElements(), responsePage.getTotalElements());
		assertEquals(categoryPage.getTotalPages(), responsePage.getTotalPages());
		assertEquals(categoryPage.getNumber(), responsePage.getNumber());
		assertEquals(categoryPage.getSize(), responsePage.getSize());

		List<CategoryResponse> expectedCategoryResponses = categoryList.stream()
			.map(CategoryResponse::new).toList();

		assertEquals(expectedCategoryResponses.size(), responsePage.getContent().size());
		assertEquals(expectedCategoryResponses.getFirst().getId(), responsePage.getContent().getFirst().getId());
		assertEquals(expectedCategoryResponses.getFirst().getName(), responsePage.getContent().getFirst().getName());
	}
}
