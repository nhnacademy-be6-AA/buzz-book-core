package store.buzzbook.core.dto.coupon;

public record CreateUserCouponRequest(

	long userId,
	String couponCode
) {
}
