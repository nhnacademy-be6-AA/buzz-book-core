package store.buzzbook.core.common.exception.order;

public class NotShippedException extends RuntimeException {
	public NotShippedException() {
		super("This order is not shipped.");
	}
}
