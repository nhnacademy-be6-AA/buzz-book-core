package store.buzzbook.core.dto.coupon;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record DeleteUserCouponRequest(

	@NotBlank(message = "쿠폰 번호는 빈 칸일 수 없습니다.")
	String couponCode
) {
}
