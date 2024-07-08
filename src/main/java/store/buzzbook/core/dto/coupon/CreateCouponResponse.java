package store.buzzbook.core.dto.coupon;

import store.buzzbook.core.entity.coupon.CouponStatus;

public record CreateCouponResponse(
	long id,
	String couponCode,
	String createDate,
	String expireDate,
	CouponStatus couponStatus,
	CouponPolicyResponse couponPolicyResponse
) {
}

