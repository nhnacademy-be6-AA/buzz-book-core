package store.buzzbook.core.dto.user;

public record DeactivateUserRequest(
	String password,
	String reason
) {
}
