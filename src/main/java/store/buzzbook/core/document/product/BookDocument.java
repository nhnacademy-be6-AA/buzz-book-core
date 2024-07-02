package store.buzzbook.core.document.product;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import store.buzzbook.core.entity.product.Book;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
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
	private LocalDate publish_date;

	@Field(type = FieldType.Integer)
	private Integer product_id;

	@Field(type = FieldType.Nested, includeInParent = true)
	private List<AuthorDocument> authors;

	public BookDocument(Book book) {
		this.id = book.getId();
		this.title = book.getTitle();
		this.description = book.getDescription();
		this.isbn = book.getIsbn();
		this.publisher_id = book.getPublisher().getId();
		this.publish_date = book.getPublishDate();
		this.product_id = book.getProduct().getId();
		this.authors = book.getBookAuthors().stream()
			.map(bookAuthor -> new AuthorDocument(bookAuthor.getAuthor())).toList();
	}
}
