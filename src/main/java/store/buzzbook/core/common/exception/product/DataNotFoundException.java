package store.buzzbook.core.common.exception.product;

public class DataNotFoundException extends RuntimeException{


	public DataNotFoundException(String DataType,int id) {
		super(String.format("%s 부분에서 id %d 값를 찾을 수 없습니댜",DataType,id));
	}
}
