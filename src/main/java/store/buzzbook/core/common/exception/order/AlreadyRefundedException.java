package store.buzzbook.core.common.exception.order;

public class AlreadyRefundedException extends RuntimeException {
	public AlreadyRefundedException() {
		super("This order has already been refunded.");
	}
}
