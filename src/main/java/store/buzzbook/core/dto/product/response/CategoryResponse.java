package store.buzzbook.core.dto.product.response;

import lombok.AllArgsConstructor;
import store.buzzbook.core.entity.product.Category;

@AllArgsConstructor
public class CategoryResponse {

	private int id;
	private String name;
	private CategoryResponse parentCategory;

	public static CategoryResponse convertToCategoryResponse(Category category){
		return new CategoryResponse(category.getId(), category.getName(), convertToCategoryResponse(category));
	}
}
