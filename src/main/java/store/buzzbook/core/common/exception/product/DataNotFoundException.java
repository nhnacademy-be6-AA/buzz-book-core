package store.buzzbook.core.common.exception.product;

import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException {

	private final String dataType;
	private final Long id;

	public DataNotFoundException(String dataType, long id) {
		super(String.format("id값 %d (으)로 %s(을)를 찾을 수 없습니다.", id, dataType));
		this.dataType = dataType;
		this.id = id;
	}

	public DataNotFoundException(String dataType, String requestType) {
		super(requestType + " (으)로 " + dataType + " 을(를) 찾을 수 없습니다.");
		this.dataType = dataType;
		this.id = null;
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
		this.dataType = null;
		this.id = null;
	}

	public DataNotFoundException(String message) {
		super(message);
		this.dataType = null;
		this.id = null;
	}

}
