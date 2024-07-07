package store.buzzbook.core.service.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.CategoryRequest;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.repository.product.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryResponse createCategory(CategoryRequest categoryRequest) {
		Category parentCategory = null;
		if (categoryRequest.getParentCategory() != null) {
			int parentId = categoryRequest.getParentCategory();
			parentCategory = categoryRepository.findById(parentId).orElseThrow(() -> new DataNotFoundException("parent category", parentId));
		}
		Category category = new Category(categoryRequest.getName(), parentCategory);
		return CategoryResponse.convertToCategoryResponse(categoryRepository.save(category));
	}

	public List<CategoryResponse> getAllCategoryResponses() {
		return categoryRepository.findAll().stream()
			.map(CategoryResponse::convertToCategoryResponse)
			.toList();
	}

	public Page<CategoryResponse> getPageableCategoryResponses(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return categoryRepository.findAll(pageable).map(CategoryResponse::convertToCategoryResponse);
	}

	public CategoryResponse updateCategory(int categoryId, CategoryRequest categoryRequest) {
		if(categoryRepository.existsById(categoryId)) {
			throw new DataNotFoundException("category", categoryId);
		}
		Category parentCategory = null;
		if (categoryRequest.getParentCategory() != null) {
			int parentId = categoryRequest.getParentCategory();
			parentCategory = categoryRepository.findById(parentId).orElseThrow(() -> new DataNotFoundException("parent category", parentId));
		}
		Category category = new Category(categoryId, categoryRequest.getName(), parentCategory);
		return CategoryResponse.convertToCategoryResponse(categoryRepository.save(category));
	}

	public void deleteCategory(int categoryId) {
		if(categoryRepository.existsById(categoryId)) {
			throw new DataNotFoundException("category", categoryId);
		}
		categoryRepository.deleteById(categoryId);
	}
}
