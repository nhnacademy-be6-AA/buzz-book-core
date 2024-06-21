package store.buzzbook.core.dto.product.response;

import lombok.*;
import store.buzzbook.core.entity.product.Product;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateRequest {
    private int stock;
    private String productName;
    private int price;
    private String thumbnailPath;
    private Product.StockStatus stockStatus;
    private int categoryId;

}
