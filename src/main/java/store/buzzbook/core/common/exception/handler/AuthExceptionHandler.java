package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import store.buzzbook.core.common.exception.auth.AuthorizeFailException;

@RestControllerAdvice
public class AuthExceptionHandler {
	@ExceptionHandler(AuthorizeFailException.class)
	public ResponseEntity<Void> handlePasswordIncorrectException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}
