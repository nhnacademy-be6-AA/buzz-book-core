package store.buzzbook.core.document.product;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aa-bb_book_index")
public class BookDocument {

	@Id
	private Long id;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String title;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String description;

	@Field(type = FieldType.Keyword)
	private String isbn;

	@Field(type = FieldType.Integer)
	private Integer publisher_id;

	@Field(type = FieldType.Date)
	private Date publish_date;

	@Field(type = FieldType.Integer)
	private Integer product_id;

	@Field(type = FieldType.Nested, includeInParent = true)
	private List<AuthorDocument> authors;

}
