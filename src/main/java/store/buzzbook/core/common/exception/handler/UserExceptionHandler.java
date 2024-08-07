package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.user.AddressMaxCountException;
import store.buzzbook.core.common.exception.user.DeactivatedUserException;
import store.buzzbook.core.common.exception.user.DormantUserException;
import store.buzzbook.core.common.exception.user.PasswordIncorrectException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
	@ExceptionHandler({UserNotFoundException.class, UserAlreadyExistsException.class})
	public ResponseEntity<Void> handleUserNotFoundException(RuntimeException e) {
		log.debug("Handling user not found exception : {}", e.getMessage());
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DormantUserException.class)
	public ResponseEntity<Void> handleDormantUserException(DormantUserException e) {
		log.debug("Handling dormant user exception : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
	}

	@ExceptionHandler(AddressMaxCountException.class)
	public ResponseEntity<Void> handleAddressMaxCountException(RuntimeException e) {
		log.debug("Handling address max count exception : {}", e.getMessage());
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DeactivatedUserException.class)
	public ResponseEntity<Void> handleDeactivatedUserException(RuntimeException e) {
		log.debug("Handling deactivated user exception : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@ExceptionHandler(PasswordIncorrectException.class)
	public ResponseEntity<Void> handlePasswordIncorrectException(RuntimeException e) {
		log.debug("Handling password incorrect exception : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

}
