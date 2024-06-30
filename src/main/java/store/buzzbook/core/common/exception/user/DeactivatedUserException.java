package store.buzzbook.core.common.exception.user;

public class DeactivatedUserException extends RuntimeException {
	public DeactivatedUserException(String loginId) {
		super(String.format("로그인 실패 : 아이디 %s는 이미 탈퇴한 계정입니다.", loginId));
	}
}
