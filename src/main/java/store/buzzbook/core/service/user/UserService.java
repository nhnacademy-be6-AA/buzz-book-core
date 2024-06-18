package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.UserInfo;

public interface UserService {
	LoginUserResponse requestLogin(Long id);
	UserInfo successLogin(Long id);
	Long requestSignUp(UserInfo userInfo);
	Long deactivate(Long id);
	Long activate(Long id);
}
