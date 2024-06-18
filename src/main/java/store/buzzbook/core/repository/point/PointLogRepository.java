package store.buzzbook.core.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.point.PointLog;

import java.util.List;

public interface PointLogRepository extends JpaRepository<PointLog, Long> {

	List<PointLog> findByUserId(Long userId);
}
