package store.buzzbook.core.common.exception.user;

public class PasswordIncorrectException extends RuntimeException {
	public PasswordIncorrectException() {
		super("비밀번호가 틀렸습니다.");
	}
}
