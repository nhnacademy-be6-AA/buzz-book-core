package store.buzzbook.core.entity.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Table(name = "book")
@Entity
@NoArgsConstructor
@Getter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
