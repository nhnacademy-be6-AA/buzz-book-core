package store.buzzbook.core.dto.cart;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import store.buzzbook.core.dto.product.TagResponse;

@Builder
@Getter
public class CartDetailResponse {
	private long id;
	private int productId;
	private String productName;
	private int quantity;
	private int price;
	private String thumbnailPath;
	private List<TagResponse> tags;
}
