package store.buzzbook.core.common.exception.order;

public class AlreadyShippingOutException extends RuntimeException {
	public AlreadyShippingOutException(String message) {
		super(message);
	}
}
