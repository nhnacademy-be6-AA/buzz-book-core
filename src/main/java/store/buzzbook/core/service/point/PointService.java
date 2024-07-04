package store.buzzbook.core.service.point;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.dto.point.CreatePointPolicyRequest;
import store.buzzbook.core.dto.point.DeletePointPolicyRequest;
import store.buzzbook.core.dto.point.PointLogResponse;
import store.buzzbook.core.dto.point.PointPolicyResponse;
import store.buzzbook.core.dto.point.UpdatePointPolicyRequest;
import store.buzzbook.core.entity.point.PointLog;

public interface PointService {

	PointPolicyResponse createPointPolicy(CreatePointPolicyRequest request);

	List<PointPolicyResponse> getPointPolicies();

	void updatePointPolicy(UpdatePointPolicyRequest request);

	void deletePointPolicy(DeletePointPolicyRequest request);

	Page<PointLogResponse> getPointLogs(Pageable pageable, Long userId);

	PointLog createPointLogWithDelta(long userId, String inquiry, int deltaPoint);
}
