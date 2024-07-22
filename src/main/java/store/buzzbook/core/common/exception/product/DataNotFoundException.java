package store.buzzbook.core.common.exception.product;

import java.util.List;

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
		super(String.format("%s (으)로 %s 을(를) 찾을 수 없습니다.", requestType, dataType));
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

	public DataNotFoundException(String dataType, List<Integer> ids) {
		super(String.format("id값 %s (으)로 %s(을)를 찾을 수 없습니다.", ids.toString(), dataType));
		this.dataType = dataType;
		this.id = null;
	}

}
