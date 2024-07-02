package store.buzzbook.core.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import store.buzzbook.core.common.exception.auth.AuthorizeFailException;

@RestControllerAdvice
public class AuthExceptionHandler {
	@ExceptionHandler(AuthorizeFailException.class)
	public ResponseEntity<ErrorResponse> handlePasswordIncorrectException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(ErrorResponse.create(e, HttpStatus.UNAUTHORIZED, e.getMessage()));
	}
}
