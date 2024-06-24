package store.buzzbook.core.dto.product.response;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Book;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BookResponse {
    private long id;
    private String title;
    private String[] authors;
    private String description;
    private String isbn;
    private String publisher;
    private ZonedDateTime publishDate;

    public static BookResponse convertToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher().toString())
                .publishDate(book.getPublishDate())
                .build();
    }
}