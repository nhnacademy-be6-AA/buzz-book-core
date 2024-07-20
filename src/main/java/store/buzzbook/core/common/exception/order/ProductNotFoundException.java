package store.buzzbook.core.common.exception.order;

public class ProductNotFoundException extends RuntimeException {
	public ProductNotFoundException() {
		super("Product not found");
	}
}
