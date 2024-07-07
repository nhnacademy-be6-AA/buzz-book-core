package store.buzzbook.core.dto.cart;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartDetailResponse {
	private long id;
	private int productId;
	private int categoryId;
	private String productName;
	private int quantity;
	private int price;
	private String thumbnailPath;
	private boolean canWrap;
}
