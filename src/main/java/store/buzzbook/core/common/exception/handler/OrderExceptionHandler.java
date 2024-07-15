package store.buzzbook.core.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.order.AddressNotFoundException;
import store.buzzbook.core.common.exception.order.AlreadyRefundedException;
import store.buzzbook.core.common.exception.order.AlreadyShippingOutException;
import store.buzzbook.core.common.exception.order.DeliveryPolicyNotFoundException;
import store.buzzbook.core.common.exception.order.ExpiredToRefundException;
import store.buzzbook.core.common.exception.order.JSONParsingException;
import store.buzzbook.core.common.exception.order.NotPaidException;
import store.buzzbook.core.common.exception.order.OrderDetailNotFoundException;
import store.buzzbook.core.common.exception.order.OrderNotFoundException;
import store.buzzbook.core.common.exception.order.OrderStatusNotFoundException;
import store.buzzbook.core.common.exception.order.ProductNotFoundException;
import store.buzzbook.core.common.exception.order.WrappingNotFoundException;

@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = {DeliveryPolicyNotFoundException.class, OrderStatusNotFoundException.class,
		ProductNotFoundException.class, WrappingNotFoundException.class, AddressNotFoundException.class,
		OrderNotFoundException.class, AddressNotFoundException.class, OrderDetailNotFoundException.class})
	public ResponseEntity<String> handleOrderNotFound(Exception ex, WebRequest request) {
		log.debug("Handling order exception : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(value = {ExpiredToRefundException.class})
	public ResponseEntity<String> handleOrderExpired(Exception ex, WebRequest request) {
		log.debug("Handling order exception : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(value = {AlreadyRefundedException.class, AlreadyShippingOutException.class})
	public ResponseEntity<String> handleOrderAlreadyExists(Exception ex, WebRequest request) {
		log.debug("Handling order exception : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(value = {NotPaidException.class})
	public ResponseEntity<String> handleOrderIllegalRequest(Exception ex, WebRequest request) {
		log.debug("Handling order exception : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(value = {JSONParsingException.class})
	public ResponseEntity<String> handleOrderParsingError(Exception ex, WebRequest request) {
		log.debug("Handling order exception : {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}
