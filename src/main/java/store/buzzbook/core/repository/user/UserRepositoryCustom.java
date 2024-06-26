package store.buzzbook.core.repository.user;

import store.buzzbook.core.entity.user.UserStatus;

public interface UserRepositoryCustom {

	boolean updateLoginDate(String loginId);

	boolean updateStatus(Long userId, UserStatus status);

	boolean updateStatus(String loginId, UserStatus status);
}
