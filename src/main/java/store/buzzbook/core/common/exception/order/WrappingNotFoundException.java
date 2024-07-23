package store.buzzbook.core.common.exception.order;

public class WrappingNotFoundException extends RuntimeException {
	public WrappingNotFoundException() {
		super("wrapping not found");
	}
}
