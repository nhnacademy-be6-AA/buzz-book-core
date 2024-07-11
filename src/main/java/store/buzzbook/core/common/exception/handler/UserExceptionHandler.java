package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import store.buzzbook.core.common.exception.user.AddressMaxCountException;
import store.buzzbook.core.common.exception.user.DeactivatedUserException;
import store.buzzbook.core.common.exception.user.DormantUserException;
import store.buzzbook.core.common.exception.user.PasswordIncorrectException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;

@RestControllerAdvice
public class UserExceptionHandler {
	@ExceptionHandler({UserNotFoundException.class, UserAlreadyExistsException.class})
	public ResponseEntity<Void> handleUserNotFoundException(RuntimeException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DormantUserException.class)
	public ResponseEntity<Void> handleDormantUserException(DormantUserException e) {
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}

	@ExceptionHandler(AddressMaxCountException.class)
	public ResponseEntity<Void> handleAddressMaxCountException(RuntimeException e) {
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DeactivatedUserException.class)
	public ResponseEntity<Void> handleDeactivatedUserException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@ExceptionHandler(PasswordIncorrectException.class)
	public ResponseEntity<Void> handlePasswordIncorrectException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

}
