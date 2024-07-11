package store.buzzbook.core.common.exception.product;

public class DataAlreadyException extends RuntimeException{

	public DataAlreadyException(String dataType, long id) {
		super(String.format("이미 존재하는 %s 입니다. (id %d)", dataType, id));
	}

	public DataAlreadyException(String dataType, String requestType) {
		super(dataType + "에 이미 " + requestType + " 값을 가진 데이터가 존재합니다.");
	}

	public DataAlreadyException(Throwable cause) {
		super(cause);
	}

	public DataAlreadyException(String message) {
		super(message);
	}
}
