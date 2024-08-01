package store.buzzbook.core.dto.coupon;

import jakarta.validation.constraints.NotBlank;

public record CouponRequest(

	@NotBlank
	String couponCode
) {
}
