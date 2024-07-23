package store.buzzbook.core.common.exception.order;

public class ExpiredToRefundException extends RuntimeException {
	public ExpiredToRefundException() {
		super("The order has expired");
	}
}
