package store.buzzbook.core.document.product;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Document(indexName = "aa-bb_author_index")
public class AuthorDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;

	@Field(type = FieldType.Object)
	private List<BookDocument> books;

}
