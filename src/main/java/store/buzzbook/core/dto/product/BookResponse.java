package store.buzzbook.core.dto.product;

import java.time.LocalDate;
import java.util.List;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Author;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.BookAuthor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BookResponse {
    private long id;
    private String title;
    private List<String> authors;
    private String description;
    private String isbn;
    private String publisher;
    private LocalDate publishDate;
    @Nullable
    private ProductResponse product;

    public static BookResponse convertToBookResponse(Book book) {

        return BookResponse.builder()
                            .id(book.getId())
                            .authors(book.getBookAuthors().stream().map(BookAuthor::getAuthor).map(Author::getName).toList())
                            .title(book.getTitle())
                            .description(book.getDescription())
                            .isbn(book.getIsbn())
                            .publisher(book.getPublisher().getName())
                            .publishDate(book.getPublishDate())
                            .product(book.getProduct() == null ? null : ProductResponse.convertToProductResponse(book.getProduct()))
                            .build();
    }
}
