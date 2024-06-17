package store.buzzbook.core.entity.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookAuthorId implements Serializable {

    private Long author;
    private Long book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookAuthorId that = (BookAuthorId) o;
        return Objects.equals(author, that.author) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, book);
    }
}
