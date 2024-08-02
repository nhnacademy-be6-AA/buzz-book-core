package store.buzzbook.core.common.exception.order;

public class OutOfPointsException extends RuntimeException {
	public OutOfPointsException() {
		super("보유 포인트가 부족합니다.");
	}
}
