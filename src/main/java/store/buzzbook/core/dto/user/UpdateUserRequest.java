package store.buzzbook.core.dto.user;

public record UpdateUserRequest(
	String name,
	String contactNumber,
	String email
) {
}
