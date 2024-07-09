package store.buzzbook.core.controller.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/api/categories")
@Tag(name = "카테고리 관리", description = "카테고리 CRUD")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/all")
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categories = categoryService.getAllCategoryResponses();
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/{id}/child")
	public ResponseEntity<List<CategoryResponse>> getChildCategories(@PathVariable int id) {
		List<CategoryResponse> childCategories = categoryService.getChildCategories(id);
		return ResponseEntity.ok(childCategories);
	}

	@GetMapping("/top")
	public ResponseEntity<List<CategoryResponse>> getTopCategories() {
		List<CategoryResponse> topCategories = categoryService.getTopCategories();
		return ResponseEntity.ok(topCategories);
	}

	@GetMapping
	public ResponseEntity<Page<CategoryResponse>> getAllCategories(
		@RequestParam(required = false) Integer pageNo,
		@RequestParam(required = false) Integer pageSize) {
		Page<CategoryResponse> page = categoryService.getPageableCategoryResponses(pageNo, pageSize);
		return ResponseEntity.ok(page);
	}

	@PostMapping
	public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) {
		CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") int id,
		@RequestBody CategoryRequest categoryRequest) {
		CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);
		return ResponseEntity.ok(updatedCategory);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable("id") int id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
