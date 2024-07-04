package store.buzzbook.core.dto.point;

import store.buzzbook.core.entity.point.PointPolicy;

public record CreatePointPolicyResponse(

	long id,
	String name,
	int point,
	double rate
) {
	public static CreatePointPolicyResponse from(PointPolicy policy) {
		return new CreatePointPolicyResponse(policy.getId(), policy.getName(), policy.getPoint(), policy.getRate());
	}
}
