package store.buzzbook.core.common.exception.order;

public class AlreadyShippingOutException extends RuntimeException {
	public AlreadyShippingOutException() {
		super("This order has already been shipping out.");
	}
}
