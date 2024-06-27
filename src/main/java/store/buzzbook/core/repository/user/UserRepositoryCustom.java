package store.buzzbook.core.repository.user;

import java.util.Optional;

import store.buzzbook.core.entity.user.Grade;

public interface UserRepositoryCustom {
	Optional<Grade> findGradeByLoginId(String loginId);
}
