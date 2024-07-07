package store.buzzbook.core.dto.point;

import jakarta.validation.constraints.NotBlank;

public record CreatePointLogRequest(

	@NotBlank
	String inquiry,
	int deltaPoint
) {
}
