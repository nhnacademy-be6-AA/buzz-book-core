package store.buzzbook.core.common.exception.order;

public class CouponStatusNotUpdatedException extends RuntimeException {
	public CouponStatusNotUpdatedException() {
		super("Coupon status not updated");
	}
}
