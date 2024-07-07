package store.buzzbook.core.dto.coupon;

import lombok.Builder;

@Builder
public record OrderCouponDetailResponse(
	String couponCode,
	int couponPolicyId,
	String couponPolicyName,
	String couponPolicyDiscountType,
	double couponPolicyDiscountRate,
	int couponPolicyDiscountAmount,
	int couponPolicyStandardPrice,
	int couponPolicyMaxDiscountAmount,
	Integer couponPolicyIdTargetId
) {
}
