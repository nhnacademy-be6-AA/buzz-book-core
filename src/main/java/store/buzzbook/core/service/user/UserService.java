package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;

public interface UserService {
	LoginUserResponse requestLogin(String loginId);
	UserInfo successLogin(Long id);
	RegisterUserResponse requestRegister(RegisterUserRequest registerUserRequest);
	Long deactivate(Long id, String reason);
	Long activate(Long id);
}
