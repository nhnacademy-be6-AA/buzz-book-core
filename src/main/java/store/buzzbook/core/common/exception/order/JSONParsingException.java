package store.buzzbook.core.common.exception.order;

public class JSONParsingException extends RuntimeException{
	public JSONParsingException() {
		super("JSON parsing error");
	}
}
