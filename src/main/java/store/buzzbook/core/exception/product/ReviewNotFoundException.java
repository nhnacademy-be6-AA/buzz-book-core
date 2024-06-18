package store.buzzbook.core.exception.product;

public class ReviewNotFoundException extends RuntimeException {
	public ReviewNotFoundException(Long reviewCode) {
		super("해당 댓글을 찾을 수 없습니다. " + reviewCode);
	}
}
