package store.buzzbook.core.common.exception.user;

public class UnEncryptedPasswordException extends RuntimeException {
	public UnEncryptedPasswordException(Long userId) {
		super(String.format("암호화 되지 않은 비밀번호입니다. userId : %s ", userId));
	}

}
