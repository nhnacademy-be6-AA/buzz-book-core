package store.buzzbook.core.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.order.DeliveryPolicyNotFoundException;
import store.buzzbook.core.common.exception.order.OrderStatusNotFoundException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.WrappingNotFoundException;

@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = {DeliveryPolicyNotFoundException.class, OrderStatusNotFoundException.class,
	ProductNotFoundException.class, WrappingNotFoundException.class})
	public ResponseEntity<String> handleOrderNotFound(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}