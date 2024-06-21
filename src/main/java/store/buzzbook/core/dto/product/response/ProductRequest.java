package store.buzzbook.core.dto.product.response;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Product;

@NoArgsConstructor
@Getter
public class ProductRequest {
	private int stock;
	private String productName;
	private int price;
	private ZonedDateTime forwardDate;
	private int score;
	private String thumbnailPath;
	private Product.StockStatus stockStatus;
	private int categoryId;
}
