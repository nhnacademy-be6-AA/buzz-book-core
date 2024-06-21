package store.buzzbook.core.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;

public interface GradeRepository extends JpaRepository<Grade, Integer> {

	Optional<Grade> findByName(GradeName name);

	boolean existsByName(GradeName name);

}
