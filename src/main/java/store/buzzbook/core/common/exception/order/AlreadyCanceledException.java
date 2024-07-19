package store.buzzbook.core.common.exception.order;

public class AlreadyCanceledException extends RuntimeException {
	public AlreadyCanceledException(String message) {
		super(message);
	}
}
