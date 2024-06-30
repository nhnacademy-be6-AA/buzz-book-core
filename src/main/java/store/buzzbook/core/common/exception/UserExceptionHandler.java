package store.buzzbook.core.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import store.buzzbook.core.common.exception.user.AddressMaxCountException;
import store.buzzbook.core.common.exception.user.DeactivatedUserException;
import store.buzzbook.core.common.exception.user.PasswordIncorrectException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;

@RestControllerAdvice
public class UserExceptionHandler {
	@ExceptionHandler({UserNotFoundException.class, UserAlreadyExistsException.class})
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(RuntimeException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage()));
	}

	@ExceptionHandler(AddressMaxCountException.class)
	public ResponseEntity<ErrorResponse> handleAddressMaxCountException(RuntimeException e) {
		return ResponseEntity.badRequest()
			.body(ErrorResponse.create(e, HttpStatus.NOT_ACCEPTABLE, e.getMessage()));
	}

	@ExceptionHandler(DeactivatedUserException.class)
	public ResponseEntity<ErrorResponse> handleDeactivatedUserException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ErrorResponse.create(e, HttpStatus.FORBIDDEN, e.getMessage()));
	}

	@ExceptionHandler(PasswordIncorrectException.class)
	public ResponseEntity<ErrorResponse> handlePasswordIncorrectException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage()));
	}

}
