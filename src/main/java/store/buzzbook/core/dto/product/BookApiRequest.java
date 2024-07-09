package store.buzzbook.core.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter

public class BookApiRequest {
    @JsonProperty("item")
    private List<Item> items;

    @Getter
    public static class Item
    {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("isbn")
        private String isbn;

        @JsonProperty("author")
        private String author;

        @JsonProperty("publisher")
        private String publisher;

        @JsonProperty("pubDate")
        private String pubDate;

        @Setter
        @JsonProperty("cover")
        private String cover;

        @JsonProperty("customerReviewRank")
        private int customerReviewRank;

        @JsonProperty("categoryName")
        private String category;

        @JsonProperty("priceStandard")
        private int priceStandard;

        @JsonProperty("priceSales")
        private int priceSales;

        @JsonProperty("stock")
        private String stock;

        @JsonProperty("product")
        private String product;
    }
}
