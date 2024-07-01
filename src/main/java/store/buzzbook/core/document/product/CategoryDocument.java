package store.buzzbook.core.document.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "aa-bb_category_index")
public class CategoryDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;

	@Field(type = FieldType.Object)
	private CategoryDocument parentCategory;

}
