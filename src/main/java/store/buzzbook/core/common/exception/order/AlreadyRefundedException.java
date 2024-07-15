package store.buzzbook.core.common.exception.order;

public class AlreadyRefundedException extends RuntimeException {
	public AlreadyRefundedException(String message) {
		super(message);
	}
}
