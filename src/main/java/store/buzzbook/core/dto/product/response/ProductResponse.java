package store.buzzbook.core.dto.product.response;

import static store.buzzbook.core.dto.product.response.CategoryResponse.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import store.buzzbook.core.entity.product.Product;

@AllArgsConstructor
@Getter
@Builder
public class ProductResponse {
	private int id;
	private int stock;
	private String productName;
	private int price;
	private LocalDate forwardDate;
	private int score;
	private String thumbnailPath;
	private Product.StockStatus stockStatus;
	private CategoryResponse category;

	public static ProductResponse convertToProductResponse(Product product) {
		return ProductResponse.builder()
			.id(product.getId())
			.stock(product.getStock())
			.productName(product.getProductName())
			.price(product.getPrice())
			.forwardDate(product.getForwardDate())
			.score(product.getScore())
			.thumbnailPath(product.getThumbnailPath())
			.stockStatus(product.getStockStatus())
			.category(convertToCategoryResponse(product.getCategory()))
			.build();
	}
}
