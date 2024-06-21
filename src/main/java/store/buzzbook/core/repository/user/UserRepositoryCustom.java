package store.buzzbook.core.repository.user;

import java.util.List;

import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;

public interface UserRepositoryCustom {
	List<UserInfo> findAllByBirthday(UserInfo userInfo);
}
