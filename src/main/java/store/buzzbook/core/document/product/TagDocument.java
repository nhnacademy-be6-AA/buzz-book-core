package store.buzzbook.core.document.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import store.buzzbook.core.entity.product.Tag;

@Document(indexName = "aa-bb_tag_index")
public class TagDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;

	public TagDocument(Tag tag) {
		id = tag.getId();
		name = tag.getName();
	}
}
