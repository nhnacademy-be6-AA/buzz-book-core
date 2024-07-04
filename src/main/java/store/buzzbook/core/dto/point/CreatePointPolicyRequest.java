package store.buzzbook.core.dto.point;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import store.buzzbook.core.entity.point.PointPolicy;

public record CreatePointPolicyRequest(

	@NotBlank(message = "포인트 정책의 이름이 비어 있을 수 없습니다.")
	String name,

	@Max(value = 50000, message = "포인트 적립액이 50000 보다 클 수 없습니다.")
	@Min(value = 0, message = "포인트 적립액이 0 보다 작을 수 없습니다.")
	int point,

	@Max(value = 1, message = "포인트 적립율이 1 보다 클 수 없습니다.")
	@Min(value = 0, message = "포인트 적립율이 0 보다 작을 수 없습니다.")
	double rate
) {
	public PointPolicy toEntity() {
		return PointPolicy.builder()
			.name(name())
			.point(point)
			.rate(rate)
			.deleted(false)
			.build();
	}
}
