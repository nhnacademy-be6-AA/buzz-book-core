package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.cart.InvalidCartUuidException;

@Slf4j
@RestControllerAdvice
public class CartExceptionHandler {
	@ExceptionHandler(InvalidCartUuidException.class)
	public ResponseEntity<Void> handlePasswordIncorrectException(RuntimeException e) {
		log.debug("Handling InvalidCartUuidException : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
}
