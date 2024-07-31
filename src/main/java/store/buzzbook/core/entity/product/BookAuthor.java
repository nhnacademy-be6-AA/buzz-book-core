package store.buzzbook.core.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.pk.BookAuthorPk;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book_author",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"author_id", "book_id"})})
public class BookAuthor {

    @EmbeddedId
    private BookAuthorPk pk;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @MapsId("authorId")
    @JsonBackReference
    private Author author;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @MapsId("bookId")
    private Book book;

    public BookAuthor(Author author, Book book) {
        this.pk = new BookAuthorPk(book.getId(), author.getId());
        this.author = author;
        this.book = book;
    }
}
