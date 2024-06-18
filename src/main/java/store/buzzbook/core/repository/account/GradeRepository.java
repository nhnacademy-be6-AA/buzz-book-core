package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.user.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
