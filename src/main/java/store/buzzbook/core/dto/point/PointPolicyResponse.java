package store.buzzbook.core.dto.point;

import store.buzzbook.core.entity.point.PointPolicy;

public record PointPolicyResponse(
	long id,
	String name,
	int point,
	double rate
) {
	public static PointPolicyResponse from(PointPolicy policy) {
		return new PointPolicyResponse(policy.getId(), policy.getName(), policy.getPoint(), policy.getRate());
	}
}
