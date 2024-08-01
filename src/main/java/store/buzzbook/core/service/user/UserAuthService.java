package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.OauthRegisterRequest;

public interface UserAuthService {
	boolean isRegistered(String provideId, String provider);

	void register(OauthRegisterRequest oauthRegisterRequest);

	LoginUserResponse getLoginUser(String provideId, String provider);
}
