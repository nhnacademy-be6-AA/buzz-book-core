package store.buzzbook.core.common.exception.user;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(Long id) {
		super(String.format("User not found: %d",id));
	}
}
