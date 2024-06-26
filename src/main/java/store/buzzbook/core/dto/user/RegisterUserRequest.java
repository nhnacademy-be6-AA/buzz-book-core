package store.buzzbook.core.dto.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
			.birthday(LocalDate.parse(request.birthday()))
			.createDate(ZonedDateTime.now())
			.email(request.email())
			.modifyDate(null)
			.contactNumber(request.contactNumber())
			.lastLoginDate(null)
			.isAdmin(false).build();

	}
}
