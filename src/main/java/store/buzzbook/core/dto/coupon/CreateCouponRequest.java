package store.buzzbook.core.dto.coupon;

import jakarta.validation.constraints.Min;
import lombok.Builder;

/**
 * 쿠폰 생성 요청 데이터를 담는 클래스입니다.
 * <p>
 * 이 클래스는 쿠폰 정책 ID를 포함하며, 해당 ID는 1 이상이어야 합니다.
 * </p>
 *
 * @param couponPolicyId 쿠폰 정책 ID
 */
@Builder
public record CreateCouponRequest(

	@Min(value = 1, message = "쿠폰 정책 ID는 1 이상이어야 합니다.")
	int couponPolicyId
) {
}
