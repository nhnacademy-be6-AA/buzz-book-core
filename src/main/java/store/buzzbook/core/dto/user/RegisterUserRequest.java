package store.buzzbook.core.dto.user;

import lombok.Builder;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;

import java.time.ZonedDateTime;


@Builder
public record RegisterUserRequest(
	String loginId,
	String password,
	String name,
	String contactNumber,
	String email,
	ZonedDateTime birthday
) {
	public static User toUser(RegisterUserRequest request){
		return User.builder()
				.loginId(request.loginId())
				.name(request.name())
				.birthday(request.birthday)
				.createDate(ZonedDateTime.now())
				.email(request.email())
				.modifyDate(null)
				.contactNumber(request.contactNumber())
				.lastLoginDate(null)
				.isAdmin(false).build();

	}
}
