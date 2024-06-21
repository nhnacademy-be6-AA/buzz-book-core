package store.buzzbook.core.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import store.buzzbook.core.entity.product.Category;

@Getter
@AllArgsConstructor
public class CategoryResponse {

	private int id;
	private String name;
	private CategoryResponse parentCategory;

	public static CategoryResponse convertToCategoryResponse(Category category) {

		return new CategoryResponse(category.getId(), category.getName(),
			category.getParentCategory()==null ? null : CategoryResponse.convertToCategoryResponse(category.getParentCategory()));
	}
}
