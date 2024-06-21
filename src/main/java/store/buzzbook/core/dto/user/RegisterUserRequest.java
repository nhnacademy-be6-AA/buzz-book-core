package store.buzzbook.core.dto.user;

import java.time.ZonedDateTime;

import lombok.Builder;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.entity.user.User;

@Builder
public record RegisterUserRequest(
	String loginId,
	String password,
	String name,
	String contactNumber,
	String email,
	String birthday
) {
	public static User toUser(RegisterUserRequest request) {
		return User.builder()
			.loginId(request.loginId())
			.name(request.name())
			.birthday(ZonedDateTimeParser.toDate(request.birthday()))
			.createDate(ZonedDateTime.now())
			.email(request.email())
			.modifyDate(null)
			.contactNumber(request.contactNumber())
			.lastLoginDate(null)
			.isAdmin(false).build();

	}
}
