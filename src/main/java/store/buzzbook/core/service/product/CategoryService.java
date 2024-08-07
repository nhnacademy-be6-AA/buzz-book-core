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

	@Transactional
	public CategoryResponse createCategory(CategoryRequest categoryRequest) {
		Integer parentId = categoryRequest.getParentCategoryId();
		Category parentCategory = null;

		if (parentId != null) {
			parentCategory = categoryRepository.findById(parentId).orElseThrow(
				() -> new DataNotFoundException("Category", parentId));
		}

		Category category = new Category(categoryRequest.getName(), parentCategory, null);
		return new CategoryResponse(categoryRepository.save(category));
	}

	@Transactional(readOnly = true)
	public Page<CategoryResponse> getPageableCategoryResponses(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return categoryRepository.findAll(pageable).map(CategoryResponse::new);
	}

	@Transactional
	public CategoryResponse updateCategory(int categoryId, CategoryRequest categoryRequest) {
		Category category = categoryRepository.findById(categoryId).orElseThrow(
			() -> new DataNotFoundException("Category", categoryId));

		// 서브 카테고리 처리
		List<Integer> subCategoryIds = categoryRequest.getSubCategoryIds();
		List<Category> subCategories = new ArrayList<>();
		if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
			subCategories = categoryRepository.findAllByIdIn(subCategoryIds);
			if (subCategories.size() != subCategoryIds.size()) {
				throw new DataNotFoundException("One or more subCategories not found.");
			}
		}

		// 부모 카테고리 처리
		Integer parentId = categoryRequest.getParentCategoryId();
		Category parentCategory = null;

		if (parentId != null) {
			parentCategory = categoryRepository.findById(parentId).orElseThrow(
				() -> new DataNotFoundException("Parent Category", parentId));
		}

		// 카테고리 업데이트
		Category updatedCategory = Category.builder()
			.id(category.getId()) // 기존 카테고리의 ID 사용
			.name(categoryRequest.getName())
			.subCategories(subCategories)
			.parentCategory(parentCategory)
			.build();

		// 업데이트된 카테고리 저장 및 응답 반환
		return new CategoryResponse(categoryRepository.save(updatedCategory));
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
		//해당카테고리의 하위카테고리를 참조하고있는 product를 조회해봐야함
		categoryRepository.deleteById(categoryId);
	}

	@Transactional
	public CategoryResponse getTopCategories() {
		List<Category> topCategories = categoryRepository.findAllByParentCategoryIsNull();
		if (topCategories.size() > 1) {
			throw new DataAlreadyException("최상위 카테고리가 여러개. \"전체\"카테고리 외 최상위 카테고리 추가 불가능");
		}
		return new CategoryResponse(topCategories.getFirst());
	}

	@Transactional
	// 상위카테고리들 + 하위 1차 카테고리들
	public CategoryResponse getSubCategoriesResponse(int categoryId) {
		return new CategoryResponse((categoryRepository.findById(categoryId).orElseThrow(
			() -> new DataNotFoundException("category", categoryId))));
	}
}
