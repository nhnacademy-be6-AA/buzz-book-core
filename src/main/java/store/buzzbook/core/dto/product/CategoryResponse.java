package store.buzzbook.core.dto.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import store.buzzbook.core.entity.product.Category;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResponse {

	private int id;
	private String name;
	private LinkedHashMap<Integer, String> parentCategory = new LinkedHashMap<>();
	private LinkedHashMap<Integer, String> subCategory;

	public CategoryResponse(Category category) {
		this.id = category.getId();
		this.name = category.getName();

		// 부모 카테고리가 null일때까지 조회
		List<Category> parents = new ArrayList<>();
		Category current = category.getParentCategory();
		while (current != null) {
			parents.add(current);
			current = current.getParentCategory();
		}

		// 역순으로 맵에 추가
		Collections.reverse(parents);
		parents.forEach(parent -> this.parentCategory.put(parent.getId(), parent.getName()));

		// 자식 카테고리 처리
		this.subCategory = new LinkedHashMap<>();
		List<Category> subCategories = category.getSubCategories();
		for (Category sub : subCategories) {
			this.subCategory.put(sub.getId(), sub.getName());
		}
	}

}
