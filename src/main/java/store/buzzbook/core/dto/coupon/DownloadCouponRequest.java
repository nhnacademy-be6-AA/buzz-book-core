package store.buzzbook.core.dto.coupon;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record DownloadCouponRequest(

	@Min(value = 1, message = "유저 번호는 1 보다 작을 수 없습니다.")
	long userId,

	@Min(value = 1, message = "쿠폰 정책 번호는 1 보다 작을 수 없습니다.")
	int couponPolicyId
) {
}
