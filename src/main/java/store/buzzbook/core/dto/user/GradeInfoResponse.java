package store.buzzbook.core.dto.user;

import lombok.Builder;

@Builder
public record GradeInfoResponse(
	String name,
	int standard,
	double benefit
) {
}
