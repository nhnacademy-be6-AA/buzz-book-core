package store.buzzbook.core.dto.product;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;

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
	private List<TagResponse> tags;

	public static ProductResponse convertToProductResponse(Product product) {
		List<TagResponse> tagResponses;

		if (product.getProductTags() != null) {

			tagResponses = product.getProductTags().stream()
				.map(ProductTag::getTag)
				.map(TagResponse::convertToTagResponse)
				.toList();
		} else {
			tagResponses = List.of();
		}

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
			.category(new CategoryResponse(product.getCategory()))
			.tags(tagResponses)
			.build();
	}
}
