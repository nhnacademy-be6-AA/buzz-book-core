package store.buzzbook.core.entity.product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "book")
@Entity
@NoArgsConstructor
@Getter
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String title;

	private String description;

	@Column(length = 13, nullable = false)
	private String isbn;

	@ManyToOne
	@JoinColumn(name = "publisher_id", nullable = false)
	private Publisher publisher;

	@Column(nullable = false)
	@JsonProperty("pubdate")
	private LocalDate publishDate;

	@Setter
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
	private List<BookAuthor> bookAuthors = new ArrayList<>();

	@Builder
	public Book(String title, String description, String isbn, Publisher publisher, String publishDate) {
		this.title = title;
		this.description = description;
		this.isbn = isbn;
		this.publisher = publisher;
		this.publishDate = LocalDate.parse(publishDate);
	}

	
}
