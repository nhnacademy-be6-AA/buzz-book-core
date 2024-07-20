package store.buzzbook.core.common.exception.order;

public class OrderStatusNotFoundException extends RuntimeException {
	public OrderStatusNotFoundException() {
		super("Order status not found");
	}
}
