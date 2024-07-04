package store.buzzbook.core.dto.point;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdatePointPolicyRequest(

	@Min(value = 1, message = "포인트 정책의 id는 1 보다 작을 수 없습니다.")
	long id,

	@Min(value = 0, message = "포인트 적립액은 0 보다 작을 수 없습니다.")
	int point,

	@Max(value = 1, message = "포인트 적립액은 1 보다 클 수 없습니다.")
	@Min(value = 0, message = "포인트 적립율은 0 보다 작을 수 없습니다.")
	double rate
) {
}
