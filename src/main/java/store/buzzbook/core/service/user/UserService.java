package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;

public interface UserService {
	LoginUserResponse requestLogin(String loginId);

	UserInfo successLogin(String loginId);

	RegisterUserResponse requestRegister(RegisterUserRequest registerUserRequest);

	boolean deactivate(Long userId, String reason);

	void activate(String loginId);

	UserInfo getUserInfoByLoginId(String loginId);

	void addUserCoupon(CreateUserCouponRequest request);
}
