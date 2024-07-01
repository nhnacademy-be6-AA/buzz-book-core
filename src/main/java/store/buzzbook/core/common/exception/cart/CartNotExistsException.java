package store.buzzbook.core.common.exception.cart;

public class CartNotExistsException extends RuntimeException {
	public CartNotExistsException(Long id) {
		super("해당 아이디의 카트는 존재하지 않습니다. : " + id);
	}

	public CartNotExistsException(String uuid) {
		super("해당 아이디의 카트는 존재하지 않습니다. : " + uuid);
	}
}
