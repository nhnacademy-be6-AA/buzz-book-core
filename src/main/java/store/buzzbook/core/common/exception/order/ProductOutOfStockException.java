package store.buzzbook.core.common.exception.order;

public class ProductOutOfStockException extends RuntimeException {
	public ProductOutOfStockException() {
		super("해당 상품의 재고가 부족합니다.");
	}
}
