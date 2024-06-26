package store.buzzbook.core.common.exception.product;

public class DataAlreadyException extends RuntimeException{


	public DataAlreadyException(String dataType, int id) {
		super(String.format("이미 존재하는 %s 입니다. (id %d)", dataType, id));
	}

	public DataAlreadyException(String dataType, long id) {
		super(String.format("이미 존재하는 %s 입니다. (id %d)", dataType, id));
	}

	public DataAlreadyException(Throwable cause) {
		super(cause);
	}

	public DataAlreadyException(String message, Throwable cause) {
		super(message, cause);
	}
}
