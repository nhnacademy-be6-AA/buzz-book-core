package store.buzzbook.core.common.exception.point;

public class PointLogNotFoundException extends RuntimeException {

	public PointLogNotFoundException() {
		super("포인트 로그를 찾을 수 없습니다.");
	}

	public PointLogNotFoundException(String message) {
		super(message);
	}
}
