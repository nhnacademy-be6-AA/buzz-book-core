package store.buzzbook.core.entity.cart;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.product.TagResponse;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.ProductTag;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "cart_detail")
public class CartDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	@NotNull
	@ColumnDefault(value = "1")
	@Min(value = 1)
	private int quantity;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	public CartDetailResponse toResponse() {
		String wrapTag = "포장가능";
		boolean canWrap = false;

		for (ProductTag tag : product.getProductTags()) {
			TagResponse tagResponse = TagResponse.convertToTagResponse(tag.getTag());
			if (tagResponse.getName().equals(wrapTag)) {
				canWrap = true;
			}
		}

		return CartDetailResponse.builder()
			.id(this.id)
			.productId(this.product.getId())
			.productName(this.product.getProductName())
			.quantity(this.getQuantity())
			.price(this.product.getPrice())
			.canWrap(canWrap)
			.thumbnailPath(this.product.getThumbnailPath())
			.categoryId(this.product.getCategory().getId())
			.build();
	}

	public void changeQuantity(int quantity) {
		this.quantity = quantity;
	}
}
