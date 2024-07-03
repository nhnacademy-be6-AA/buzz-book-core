package store.buzzbook.core.common.exception.order;

public class OrderStatusNotFoundException extends RuntimeException {
	public OrderStatusNotFoundException(String message) {
		super(message);
	}
}
