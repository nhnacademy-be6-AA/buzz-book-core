package store.buzzbook.core.controller.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.CategoryRequest;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.service.product.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/categories")
@Tag(name = "카테고리 조회", description = "카테고리 리스트반환")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/all")
	public List<CategoryResponse> getAllCategories() {
		return categoryService.getAllCategoryResponses();
	}

	@GetMapping
	public Page<CategoryResponse> getAllCategories(
		@RequestParam(required = false) Integer pageNo,
		@RequestParam(required = false) Integer pageSize) {
		return categoryService.getPageableCategoryResponses(pageNo, pageSize);
	}

	@PostMapping
	public CategoryResponse createCategory(@RequestBody CategoryRequest categoryRequest) {
		return categoryService.createCategory(categoryRequest);
	}

	@PostMapping("/{id}")
	public CategoryResponse updateCategory(@PathVariable("id") int id, @RequestBody CategoryRequest categoryRequest) {
		return categoryService.updateCategory(id, categoryRequest);
	}

	@DeleteMapping("/{id}")
	public void deleteCategory(@PathVariable("id") int id) {
		categoryService.deleteCategory(id);
	}
}
