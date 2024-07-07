package store.buzzbook.core.repository.user;

import java.util.List;
import java.util.Optional;

import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.User;

public interface UserRepositoryCustom {
	Optional<Grade> findGradeByLoginId(String loginId);

	Optional<Grade> findGradeByUserId(Long userId);

	List<User> findUsersByBirthdayInCurrentMonth();
}
