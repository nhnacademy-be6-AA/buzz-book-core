package store.buzzbook.core.dto.coupon;

public record CreateWelcomeCouponResponse(

	int resultCode,
	long userId,
	long couponId
) {
}
