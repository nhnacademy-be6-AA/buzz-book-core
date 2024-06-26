package store.buzzbook.core.common.exception.user;

public class GradeNotFoundException extends RuntimeException {
	public GradeNotFoundException(String name) {
		super(String.format("등급 이름 '%s' : 찾을 수 없습니다. DB를 확인해주세요. ", name));
	}

	public GradeNotFoundException(Long userId) {
		super(String.format("회원 id로 등록된 등급을 찾을 수 없습니다. DB를 확인해주세요. : %d ", userId));
	}
}
