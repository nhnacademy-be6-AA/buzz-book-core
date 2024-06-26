package store.buzzbook.core.common.exception.user;

public class AddressMaxCountException extends RuntimeException {
	public AddressMaxCountException(Long userId) {
		super(String.format("해당 회원의 주소는 이미 10개입니다. user Id : %d", userId));
	}
}
