package store.buzzbook.core.common.exception.order;

public class AddressNotFoundException extends RuntimeException {
	public AddressNotFoundException() {
		super("Address not found");
	}
}
