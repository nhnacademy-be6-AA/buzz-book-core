package store.buzzbook.core.common.exception.cart;

public class InvalidCartUuidException extends RuntimeException {
	public InvalidCartUuidException() {
		super("유효하지 않은 cart uuid 요청입니다.");
	}
}
