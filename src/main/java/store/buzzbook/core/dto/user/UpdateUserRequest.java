package store.buzzbook.core.dto.user;

import java.time.ZonedDateTime;

public record UpdateUserRequest(
	String loginId,
	String name,
	String contactNumber,
	String email,
	ZonedDateTime birthday
) {
}
