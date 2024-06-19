package store.buzzbook.core.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.point.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
}
