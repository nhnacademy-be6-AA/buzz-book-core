package store.buzzbook.core.dto.product;

import java.util.ArrayList;
import java.util.List;

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
	private Integer parentCategoryId;
	private String parentCategoryName;
	private List<CategoryResponse> subCategories = new ArrayList<>();

	public static CategoryResponse convertToCategoryResponse(Category category) {

		return new CategoryResponse(
			category.getId(),
			category.getName(),
			category.getParentCategory()==null? null : category.getParentCategory().getId(),
			category.getParentCategory()==null? null : category.getParentCategory().getName(),
			category.getSubCategories().stream().map(CategoryResponse::convertToCategoryResponse).toList());
	}

	public static CategoryResponse convertSub1ToCategoryResponse(Category category){

		List<CategoryResponse> subCategoriesResponse = category.getSubCategories().stream()
			.map(subCategory -> new CategoryResponse(
				subCategory.getId(),
				subCategory.getName(),
				subCategory.getParentCategory().getId(),
				subCategory.getParentCategory().getName(),
				List.of()
			))
			.toList();

		return new CategoryResponse(
			category.getId(),
			category.getName(),
			category.getParentCategory()==null? null : category.getParentCategory().getId(),
			category.getParentCategory()==null? null : category.getParentCategory().getName(),
			subCategoriesResponse);
	}
}
