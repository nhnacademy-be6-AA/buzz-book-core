package store.buzzbook.core.common.exception.order;

public class OutOfCouponException extends RuntimeException {
	public OutOfCouponException() {
		super("해당 쿠폰을 보유하고 있지 않습니다.");
	}
}
