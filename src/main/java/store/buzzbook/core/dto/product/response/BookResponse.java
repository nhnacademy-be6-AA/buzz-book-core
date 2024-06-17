package store.buzzbook.core.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookResponse {
    private String title;
    private String author;
    private String publisher;
    private String publishDate;
    private String isbn;
    private String description;
    //private List<String> categories;

}