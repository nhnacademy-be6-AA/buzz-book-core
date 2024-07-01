package store.buzzbook.core.document.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aa-bb_publisher_index")
public class PublisherDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;
}
