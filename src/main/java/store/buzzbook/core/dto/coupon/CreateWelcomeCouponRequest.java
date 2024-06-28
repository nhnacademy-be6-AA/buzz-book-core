package store.buzzbook.core.dto.coupon;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record CreateWelcomeCouponRequest(

	@Min(1)
	long userId
) {
}
