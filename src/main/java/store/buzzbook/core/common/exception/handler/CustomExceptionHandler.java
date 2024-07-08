package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.product.DataAlreadyException;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.review.IllegalRequestException;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
	@ExceptionHandler(DataAlreadyException.class)
	public ResponseEntity<String> handleDataAlreadyException(DataAlreadyException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<String> handleDataNotFoundException(DataNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(IllegalRequestException.class)
	public ResponseEntity<String> handleIllegalRequestException(IllegalRequestException ex) {
		return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ex.getMessage());
	}
}
