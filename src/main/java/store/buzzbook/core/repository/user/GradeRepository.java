package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByName(GradeName name);
}
