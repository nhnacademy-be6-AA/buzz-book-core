package store.buzzbook.core.common.exception.order;

public class NotShippedException extends RuntimeException {
	public NotShippedException(String message) {
		super(message);
	}
}
