package store.buzzbook.core.common.exception.order;

public class ProductNotFoundException extends RuntimeException {
	public ProductNotFoundException(String message) {
		super(message);
	}
}
