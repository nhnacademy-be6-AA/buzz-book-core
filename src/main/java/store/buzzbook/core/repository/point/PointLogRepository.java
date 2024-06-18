package store.buzzbook.core.repository.point;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.point.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {

	List<PointLog> findByUserId(Long userId);
}
