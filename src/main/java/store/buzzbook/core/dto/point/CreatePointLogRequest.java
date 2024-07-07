package store.buzzbook.core.dto.point;

import jakarta.validation.constraints.NotBlank;

public record CreatePointLogRequest(

	@NotBlank(message = "적립 사유는 빈 칸일 수 없습니다.")
	String inquiry,

	int deltaPoint
) {
}
