package store.buzzbook.core.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductUpdateRequest {
    @NotNull(message = "재고량을 입력하세요.")
    private int stock;

    @NotBlank(message = "상품명을 입력하세요.")
    private String productName;

    private String description;

    @NotNull(message = "가격을 입력하세요.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    private int price;

    private String thumbnailPath;

    @NotBlank(message = "재고 상태를 입력하세요.")
    private Product.StockStatus stockStatus;

    @NotNull
    private int categoryId;
}
