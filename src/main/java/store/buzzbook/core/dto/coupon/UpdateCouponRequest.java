package store.buzzbook.core.dto.coupon;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import store.buzzbook.core.entity.coupon.CouponStatus;

@Builder
public record UpdateCouponRequest(

	@NotBlank(message = "쿠폰 코드는 null 일 수 없습니다.")
	String couponCode,

	@NotBlank(message = "status 는 null 일 수 없습니다.")
	CouponStatus status
) {

}
