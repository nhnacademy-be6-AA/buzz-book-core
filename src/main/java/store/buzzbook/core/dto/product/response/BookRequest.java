package store.buzzbook.core.dto.product.response;

import java.time.ZonedDateTime;

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
    private ZonedDateTime publishDate;
    @Nullable
    private Integer productId;
}
