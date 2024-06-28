package store.buzzbook.core.common.exception.user;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException() {
		super("회원을 찾을 수 없습니다.");
	}

	public UserNotFoundException(String loginId) {
		super(String.format("회원을 찾을 수 없습니다. login id: %s", loginId));
	}

	public UserNotFoundException(Long userId) {
		super(String.format("회원을 찾을 수 없습니다. user id: %s", userId));
	}

}
