package store.buzzbook.core.common.exception.order;

public class ExpiredToRefundException extends RuntimeException {
	public ExpiredToRefundException(String message) {
		super(message);
	}
}
