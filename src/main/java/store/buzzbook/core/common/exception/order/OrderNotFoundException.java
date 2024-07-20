package store.buzzbook.core.common.exception.order;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException() {
		super("Order not found");
	}
}
