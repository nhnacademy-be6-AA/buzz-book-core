package store.buzzbook.core.dto.point;

import jakarta.validation.constraints.Min;

public record DeletePointPolicyRequest(

	@Min(value = 1, message = "포인트 정책 번호는 1 보다 작을 수 없습니다.")
	long id
) {
}
