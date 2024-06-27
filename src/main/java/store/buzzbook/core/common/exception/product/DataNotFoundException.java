package store.buzzbook.core.common.exception.product;

public class DataNotFoundException extends RuntimeException {


	public DataNotFoundException(String dataType, long id) {
		super(String.format("id값 %d (으)로 %s(을)를 찾을 수 없습니다.", id, dataType));
	}

	public DataNotFoundException(String dataType, String requestType) {
		super(requestType + " (으)로 " + dataType + " 을(를) 찾을 수 없습니다.");
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
	}

	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
