package store.buzzbook.core.dto.point;

import java.time.LocalDateTime;

import store.buzzbook.core.entity.point.PointLog;

public record PointLogResponse(
	LocalDateTime createdAt,
	String inquiry,
	int delta,
	int balance
) {
	public static PointLogResponse from(PointLog pointLog) {
		return new PointLogResponse(
			pointLog.getCreatedAt(),
			pointLog.getInquiry(),
			pointLog.getDelta(),
			pointLog.getBalance());
	}
}
