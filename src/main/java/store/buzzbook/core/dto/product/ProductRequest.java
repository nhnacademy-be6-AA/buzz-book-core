package store.buzzbook.core.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.product.Product;

@NoArgsConstructor
@Getter
public class ProductRequest {
	private int stock;
	private String productName;
	private String description;
	private int price;
	private String forwardDate; //("YYYY-MM-DD")형식 or validation 필요
	private int score;
	private String thumbnailPath;
	private Product.StockStatus stockStatus;
	private int categoryId;
}
