package store.buzzbook.core.dto.product.response;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BookRequest {
    private String title;
    private String description;
    private String isbn;
    private String publisher;
    private String publishDate; //("YYYY-MM-DD")형식 or validation 필요
    @Nullable
    private Integer productId;
}
