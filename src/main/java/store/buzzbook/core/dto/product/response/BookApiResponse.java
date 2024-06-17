package store.buzzbook.core.dto.product.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BookApiResponse {
    @JsonProperty("item")
    private List<Item> items;

    @Getter
    public static class Item {
        @JsonProperty("title")
        private String title;

        @JsonProperty("author")
        private String author;

        @JsonProperty("publisher")
        private String publisher;

        @JsonProperty("pubDate")
        private String pubDate;

        @JsonProperty("isbn")
        private String isbn;

        @JsonProperty("description")
        private String description;

        @JsonProperty("cover")
        private String cover;
    }
}