package store.buzzbook.core.dto.user;

import lombok.Builder;
import store.buzzbook.core.entity.user.User;

@Builder
public record LoginUserResponse(String loginId, String password, boolean isAdmin) {
	public static LoginUserResponse convertFrom(User user) {
		return LoginUserResponse.builder()
			.loginId(user.getLoginId())
			.password(user.getPassword()).build();
	}
}
