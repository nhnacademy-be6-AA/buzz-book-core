package store.buzzbook.core.common.exception.user;

public class DormantUserException extends RuntimeException {
	public DormantUserException() {
		super("휴면 계정의 로그인 요청입니다.");
	}
}
