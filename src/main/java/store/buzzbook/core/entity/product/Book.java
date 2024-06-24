package store.buzzbook.core.entity.product;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "book")
@Entity
@NoArgsConstructor
@Getter
@Setter
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
	private ZonedDateTime publishDate;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Builder
	public Book(String title, String description, String isbn, Publisher publisher, String publishDate) {
		this.title = title;
		this.description = description;
		this.isbn = isbn;
		this.publisher = publisher;
		LocalDate localDate = LocalDate.parse(publishDate);
		this.publishDate = localDate.atStartOfDay(ZoneId.systemDefault());
	}
}
