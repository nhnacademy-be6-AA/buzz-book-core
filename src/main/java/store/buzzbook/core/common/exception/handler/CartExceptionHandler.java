package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import store.buzzbook.core.common.exception.cart.InvalidCartUuidException;

@RestControllerAdvice
public class CartExceptionHandler {
	@ExceptionHandler(InvalidCartUuidException.class)
	public ResponseEntity<ErrorResponse> handlePasswordIncorrectException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ErrorResponse.create(e, HttpStatus.FORBIDDEN, e.getMessage()));
	}
}
