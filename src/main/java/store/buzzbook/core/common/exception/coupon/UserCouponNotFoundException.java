package store.buzzbook.core.common.exception.coupon;

public class UserCouponNotFoundException extends RuntimeException {

	public UserCouponNotFoundException() {
		super("쿠폰 기록이 존재하지 않습니다.");
	}

	public UserCouponNotFoundException(String message) {
		super(message);
	}
}
