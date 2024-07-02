package store.buzzbook.core.controller.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.CategoryResponse;
import store.buzzbook.core.service.product.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "카테고리 조회", description = "카테고리 리스트반환")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public List<CategoryResponse> getAllCategories() {
		return categoryService.getAllCategoryResponses();
	}
}