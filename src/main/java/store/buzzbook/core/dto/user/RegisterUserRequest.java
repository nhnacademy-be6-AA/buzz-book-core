package store.buzzbook.core.dto.user;

import java.time.ZonedDateTime;

import lombok.Builder;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

@Builder
public record RegisterUserRequest(
	String loginId,
	String password,
	String name,
	String contactNumber,
	String email,
	String birthday
) {
	public User toUser() {
		return User.builder()
			.loginId(this.loginId())
			.name(this.name())
			.password(this.password())
			.birthday(ZonedDateTimeParser.toDate(this.birthday()))
			.createDate(ZonedDateTime.now())
			.email(this.email())
			.modifyDate(ZonedDateTime.now())
			.contactNumber(this.contactNumber())
			.lastLoginDate(null)
			.status(UserStatus.ACTIVE)
			.isAdmin(false).build();

	}
}
