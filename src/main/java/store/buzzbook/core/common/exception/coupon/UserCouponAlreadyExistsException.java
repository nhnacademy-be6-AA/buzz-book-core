package store.buzzbook.core.common.exception.coupon;

public class UserCouponAlreadyExistsException extends RuntimeException {

	public UserCouponAlreadyExistsException() {
		super("유저가 해당 쿠폰을 이미 다운로드 했습니다.");
	}

	public UserCouponAlreadyExistsException(String message) {
		super(message);
	}
}
