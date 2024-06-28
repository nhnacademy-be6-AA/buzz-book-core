package store.buzzbook.core.dto.user;

import java.time.LocalDate;

public record UpdateUserRequest(
	String loginId,
	String name,
	String contactNumber,
	String email,
	LocalDate birthday
) {
}
