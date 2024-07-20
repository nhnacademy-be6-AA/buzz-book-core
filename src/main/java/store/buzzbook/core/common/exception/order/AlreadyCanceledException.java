package store.buzzbook.core.common.exception.order;

public class AlreadyCanceledException extends RuntimeException {
	public AlreadyCanceledException() {
		super("This order has already been Canceled");
	}
}
