package store.buzzbook.core.repository.user;

import java.util.Optional;

import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.UserStatus;

public interface UserRepositoryCustom {
	Optional<Grade> findGradeByUserId(Long userId);

	boolean updateStatus(Long userId, UserStatus status);

	boolean updateStatus(String loginId, UserStatus status);
}
