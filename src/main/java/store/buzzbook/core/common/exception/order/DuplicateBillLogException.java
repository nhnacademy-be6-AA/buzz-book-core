package store.buzzbook.core.common.exception.order;

public class DuplicateBillLogException extends RuntimeException {
	public DuplicateBillLogException() {
		super("Duplicated bill log");
	}
}
