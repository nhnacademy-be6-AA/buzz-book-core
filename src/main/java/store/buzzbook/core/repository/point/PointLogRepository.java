package store.buzzbook.core.repository.point;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.point.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {

	List<PointLog> findByUserId(long userId);

	Page<PointLog> findByUserId(long userId, Pageable pageable);

	//balance값을 알기위해 userId로 user의 가장 최근의 포인트 로그값 조회
	PointLog findLast1ByUserId(long userId);

	PointLog findFirstByUserIdOrderByCreatedAtDesc(long userId);
}
