package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.GradeLog;

public interface GradeLogRepository extends JpaRepository<GradeLog, Long> {
}
