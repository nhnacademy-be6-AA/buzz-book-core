package store.buzzbook.core.elastic.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import store.buzzbook.core.entity.product.Author;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Document(indexName = "aa-bb_author_index")
public class AuthorDocument {

	@Id
	private Integer id;

	@Field(type = FieldType.Keyword)
	private String name;

	@Field(type = FieldType.Object)
	private List<BookDocument> books;

	public AuthorDocument(Author author) {
		this.id = author.getId();
		this.name = author.getName();
		this.books = author.getBookAuthors().stream()
			.map(bookAuthor -> new BookDocument(bookAuthor.getBook())).toList();
	}
}
