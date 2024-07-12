package store.buzzbook.core.elastic.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import store.buzzbook.core.entity.product.Category;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Document(indexName = "aa-bb_category_index")
public class CategoryDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;

	@Field(type = FieldType.Object)
	private CategoryDocument parentCategory;

	public CategoryDocument(Category category) {
		this.id = category.getId();
		this.name = category.getName();
		this.parentCategory = new CategoryDocument(category.getParentCategory());
	}

}
