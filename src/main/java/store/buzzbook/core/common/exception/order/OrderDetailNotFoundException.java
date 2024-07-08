package store.buzzbook.core.common.exception.order;

public class OrderDetailNotFoundException extends RuntimeException {
	public OrderDetailNotFoundException(String message) {
		super(message);
	}
}
