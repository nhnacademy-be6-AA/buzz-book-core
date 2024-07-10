package store.buzzbook.core.entity.product;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory"
		//, fetch = FetchType.LAZY
	)
	private List<Category> subCategories = new ArrayList<>();

	public Category(String name, Category parentCategory, List<Category> subCategories) {
		this.name = name;
		this.parentCategory = parentCategory;
		if(subCategories == null) {
			subCategories = new ArrayList<>();
		}
		this.subCategories = subCategories;
	}
}
