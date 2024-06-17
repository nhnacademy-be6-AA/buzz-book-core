package store.buzzbook.core.entity.product;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@IdClass(BookAuthorId.class)
public class BookAuthor {
    @Id
    @ManyToOne
    @JoinColumn(name = "author_id",nullable = false)
    private Author author;
    @ManyToOne
    @JoinColumn(name = "book_id",nullable = false)
    private Book book;
}
