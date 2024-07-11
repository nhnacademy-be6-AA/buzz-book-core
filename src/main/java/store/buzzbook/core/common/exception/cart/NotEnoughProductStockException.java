package store.buzzbook.core.common.exception.cart;

public class NotEnoughProductStockException extends RuntimeException {
	public NotEnoughProductStockException() {
		super("상품의 재고가 0 이하입니다.");
	}
}
