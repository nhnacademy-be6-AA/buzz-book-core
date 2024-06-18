package store.buzzbook.core.dto.user;

import java.time.ZonedDateTime;

public record RegisterUserRequest(
	String loginId,
	String password,
	String name,
	String contactNumber,
	String email,
	ZonedDateTime birthday
) {
}
