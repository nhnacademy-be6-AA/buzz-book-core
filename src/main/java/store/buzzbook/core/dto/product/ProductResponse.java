package store.buzzbook.core.dto.product;

import static store.buzzbook.core.dto.product.CategoryResponse.*;

import java.time.LocalDate;

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
	private String description;
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
			.description(product.getDescription())
			.price(product.getPrice())
			.forwardDate(product.getForwardDate())
			.score(product.getScore())
			.thumbnailPath(product.getThumbnailPath())
			.stockStatus(product.getStockStatus())
			.category(convertToCategoryResponse(product.getCategory()))
			.build();
	}
}
