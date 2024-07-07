package store.buzzbook.core.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import store.buzzbook.core.entity.product.Category;

@ToString
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
