package store.buzzbook.core.common.exception.user;

public class UnknownUserException extends RuntimeException {
	public UnknownUserException(String message) {
		super(String.format("User api : 알 수 없는 오류가 발생했습니다. 메시지 : %s",message));
	}
}
