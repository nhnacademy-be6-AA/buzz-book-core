package store.buzzbook.core.common.exception.product;

public class DataNotFoundException extends RuntimeException {

	public DataNotFoundException(String dataType, int id) {
		super(String.format("%s 부분에서 id %d 값을 찾을 수 없습니다 " , dataType, id));
	}

	public DataNotFoundException(String dataType, long id) {
		super(String.format("%s 부분에서 id %d 값을 찾을 수 없습니다", dataType, id));
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
	}

	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
