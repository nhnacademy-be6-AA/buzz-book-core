package store.buzzbook.core.repository.user;

import java.util.List;

import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.UserStatus;

public interface UserRepositoryCustom {
	List<UserInfo> findAllByBirthday(UserInfo userInfo);

	boolean updateLoginDate(String loginId);

	boolean updateStatus(String loginId, UserStatus status);
}
