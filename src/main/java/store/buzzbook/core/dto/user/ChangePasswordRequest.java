package store.buzzbook.core.dto.user;

public record ChangePasswordRequest(
	String oldPassword,
	String newPassword,
	String confirmPassword
) {

}
