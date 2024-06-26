package store.buzzbook.core.dto.cart;

import jakarta.validation.constraints.NotNull;
import store.buzzbook.core.entity.cart.Cart;
import store.buzzbook.core.entity.cart.CartDetail;
import store.buzzbook.core.entity.product.Product;

public record CreateCartDetailRequest(
	@NotNull
	Long userId,
	@NotNull
	Long cartId,
	@NotNull
	Integer quantity,
	@NotNull
	Integer productId) {

	public CartDetail toCartDetail(Cart cart, Product product) {
		return CartDetail.builder()
			.cart(cart)
			.product(product)
			.quantity(this.quantity)
			.build();
	}
}
