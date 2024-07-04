package store.buzzbook.core.common.exception.point;

public class PointPolicyNotFoundException extends RuntimeException {

	public PointPolicyNotFoundException() {
		super("포인트 정책을 찾을 수 없습니다.");
	}

	public PointPolicyNotFoundException(String message) {
		super(message);
	}
}
