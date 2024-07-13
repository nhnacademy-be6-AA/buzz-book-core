package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {
	@ExceptionHandler(AuthorizeFailException.class)
	public ResponseEntity<Void> handlePasswordIncorrectException(RuntimeException e) {
		log.debug("Authorization exception : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}
