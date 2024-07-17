package store.buzzbook.core.dto.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Product;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductRequest {
	@NotNull(message = "재고량을 입력하세요.")
	private int stock;

	@NotBlank(message = "상품명을 입력하세요.")
	private String productName;

	private String description;

	@NotNull(message = "가격을 입력하세요.")
	@PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
	private int price;

	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 YYYY-MM-DD 여야 합니다")
	private String forwardDate;

	private String thumbnailPath;

	@NotBlank(message = "재고 상태를 입력하세요.")
	private Product.StockStatus stockStatus;

	@NotNull
	private int categoryId;

	private List<String> tags;
}
