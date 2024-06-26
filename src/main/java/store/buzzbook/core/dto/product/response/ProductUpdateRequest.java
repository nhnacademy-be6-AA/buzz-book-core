package store.buzzbook.core.dto.product.response;

import org.springframework.lang.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Product;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateRequest {
    private int stock;
    private String productName;
    @Nullable
    private String description;
    private int price;
    private String thumbnailPath;
    private Product.StockStatus stockStatus;
    private int categoryId;

}
