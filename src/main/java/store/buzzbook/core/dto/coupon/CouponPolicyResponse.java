package store.buzzbook.core.dto.coupon;

import java.time.LocalDate;

public record CouponPolicyResponse(
	int id,
	String name,
	String discountType,
	double discountRate,
	int discountAmount,
	int standardPrice,
	int maxDiscountAmount,
	int period,
	LocalDate startDate,
	LocalDate endDate,
	boolean isDeleted,
	CouponTypeResponse couponTypeResponse
) {
}
