package store.buzzbook.core.elastic.document;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "aa-bb_book_index")
@AllArgsConstructor
public class BookDocument {

	@Id
	private long bookId;

	private int productId;

	@Field(type = FieldType.Keyword)
	private String isbn;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String bookTitle;

	@Field(type = FieldType.Text, analyzer = "nori")
	private String description;

	@Field(type = FieldType.Date)
	private LocalDate forwardDate;

	@Field(type = FieldType.Text, analyzer = "nori")
	private List<String> authors;

}