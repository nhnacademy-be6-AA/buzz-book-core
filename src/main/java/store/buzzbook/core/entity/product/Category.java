package store.buzzbook.core.entity.product;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = {"id", "name"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, length = 20)
	private String name; //카테고리명

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)
	private List<Category> subCategories = new ArrayList<>();

	public Category(String name, Category parentCategory, List<Category> subCategories) {
		this.name = name;
		this.parentCategory = parentCategory;
		if(subCategories == null) {
			subCategories = new ArrayList<>();
		}
		this.subCategories = subCategories;
	}

	public List<Integer> getAllSubCategoryIdsIterative() {
		List<Integer> allSubCategoryIds = new ArrayList<>();
		Deque<Category> stack = new ArrayDeque<>();
		stack.push(this);

		while (!stack.isEmpty()) {
			Category currentCategory = stack.pop();
			allSubCategoryIds.add(currentCategory.getId());

			for (Category subCategory : currentCategory.getSubCategories()) {
				stack.push(subCategory);
			}
		}
		return allSubCategoryIds;
	}
}
