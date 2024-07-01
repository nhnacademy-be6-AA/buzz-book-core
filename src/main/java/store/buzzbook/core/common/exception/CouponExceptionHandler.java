package store.buzzbook.core.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.coupon.UserCouponAlreadyExistsException;

@Slf4j
@RestControllerAdvice
public class CouponExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = UserCouponAlreadyExistsException.class)
	public ResponseEntity<String> handleUserCouponAlreadyExistsException(UserCouponAlreadyExistsException e) {
		log.debug("handleBadRequest : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}
}
