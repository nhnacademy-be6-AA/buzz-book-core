package store.buzzbook.core.common.exception.order;

public class NotPaidException extends RuntimeException {
	public NotPaidException() {
		super("This order is not paid");
	}
}
