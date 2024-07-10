package store.buzzbook.core.service.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataAlreadyException;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.review.IllegalRequestException;
import store.buzzbook.core.dto.product.CategoryRequest;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	public CategoryResponse createCategory(CategoryRequest categoryRequest) {
		Category parentCategory = null;
		if (categoryRequest.getParentCategory() != null) {
			int parentId = categoryRequest.getParentCategory();
			parentCategory = categoryRepository.findById(parentId)
				.orElseThrow(() -> new DataNotFoundException("parent category", parentId));
		}
		Category category = new Category(categoryRequest.getName(), parentCategory, null);
		return CategoryResponse.convertToCategoryResponse(categoryRepository.save(category));
	}

	public Page<CategoryResponse> getPageableCategoryResponses(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return categoryRepository.findAll(pageable).map(CategoryResponse::convertToCategoryResponse);
	}

	public CategoryResponse updateCategory(int categoryId, CategoryRequest categoryRequest) {

		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new DataNotFoundException("category", categoryId));

		List<Integer> subCategoryIds = categoryRequest.getSubCategories();
		List<Category> subCategories = new ArrayList<>();
		if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
			subCategories = categoryRepository.findAllByIdIn(subCategoryIds);
			if (subCategories.size() != subCategoryIds.size()) {
				throw new DataNotFoundException("subCategory 중 찾을수 없는 값이 있다.");
			}
		}

		Category parentCategory = null;
		if (categoryRequest.getParentCategory() != null) {
			int parentId = categoryRequest.getParentCategory();
			parentCategory = categoryRepository.findById(parentId)
				.orElseThrow(() -> new DataNotFoundException("parent category", parentId));
		}

		Category updateCategory = Category.builder()
			.id(category.getId())
			.name(categoryRequest.getName())
			.subCategories(subCategories)
			.parentCategory(parentCategory)
			.build();

		return CategoryResponse.convertToCategoryResponse(categoryRepository.save(updateCategory));
	}

	@Transactional
	public void deleteCategory(int categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new DataNotFoundException("category", categoryId);
		}
		List<Product> products = productRepository.findByCategoryId(categoryId);
		if (!products.isEmpty()) {
			throw new IllegalRequestException("해당 카테고리로 분류된 상품이 있어 카테고리를 삭제할 수 없습니다.");
		}

		//TODO 해당카테고리의 하위카테고리를 참조하고있는 product를 조회해봐야함 + 중간 카테고리를 날리면 하위 카테고리들은 모두 최상위 카테고리가 될듯?
		categoryRepository.deleteById(categoryId);
	}




	public CategoryResponse getTopCategories() {
		List<Category> topCategories = categoryRepository.findAllByParentCategoryIsNull();
		if (topCategories.size() > 2) {
			throw new DataAlreadyException("왜 최상위 카테고리가 여러개지?");
		}
		return CategoryResponse.convertSub1ToCategoryResponse(topCategories.getFirst());
	}


	// 1차 하위 카테고리들
	public CategoryResponse getSubCategoriesResponse(int categoryId) {
		return CategoryResponse.convertSub1ToCategoryResponse(categoryRepository.findById(categoryId).orElseThrow(
			() -> new DataNotFoundException("category", categoryId)
		));
	}

	// 모든 하위 카테고리들
	public CategoryResponse getAllSubCategories(int categoryId) {
		return CategoryResponse.convertToCategoryResponse(categoryRepository.findById(categoryId).orElseThrow(
			() -> new DataNotFoundException("category", categoryId)
		));
	}

	public List<Integer> getSubAllCategoryIds(int categoryId) {
		return categoryRepository.findAllByIdIn(List.of(categoryId)).stream().map(Category::getId).toList();
	}

	// // (카테고리들의) 1차 하위 카테고리들
	// public List<Category> getSubCategories(List<Integer> categoryIds) {
	// 	return categoryRepository.findAllByParentCategoryIdIn(categoryIds);
	// }

	// @Transactional(readOnly = true)
	// public List<Category> findAllSubcategories(int categoryId) {
	// 	Category category = categoryRepository.findById(categoryId)
	// 		.orElseThrow(() -> new DataNotFoundException("category", categoryId));
	// 	List<Category> categoryList = new ArrayList<>();
	// 	categoryList.add(category);
	// 	findAllSubcategoriesIterative(categoryId, categoryList);
	// 	return categoryList;
	// }

	// 재귀라 삭제
	// private void findAllSubcategoriesRecursive(int categoryId, List<Category> categoryList) {
	// 	if (categoryRepository.existsById(categoryId)) {
	// 		List<Category> subCategoryList = categoryRepository.findAllByParentCategoryIdIn(List.of(categoryId));
	// 		for (Category subcategory : subCategoryList) {
	// 			categoryList.add(subcategory);
	// 			findAllSubcategoriesRecursive(subcategory.getId(), categoryList);
	// 		}
	// 	}
	// }

	// private void findAllSubcategoriesIterative(int categoryId, List<Category> categoryList) {
	// 	Queue<Integer> queue = new LinkedList<>();
	// 	queue.add(categoryId);
	//
	// 	// Process categories until the queue is empty
	// 	while (!queue.isEmpty()) {
	// 		int currentCategoryId = queue.poll(); // Get the next category ID from the queue
	//
	// 		if (categoryRepository.existsById(currentCategoryId)) { // Check if the category exists
	// 			// Retrieve subcategories of the current category
	// 			List<Category> subCategoryList = categoryRepository.findAllByParentCategoryIdIn(
	// 				List.of(currentCategoryId));
	// 			for (Category subcategory : subCategoryList) {
	// 				categoryList.add(subcategory); // Add the subcategory to the result list
	// 				queue.add(subcategory.getId()); // Add the subcategory ID to the queue for further processing
	// 			}
	// 		}
	// 	}
	// }
}
