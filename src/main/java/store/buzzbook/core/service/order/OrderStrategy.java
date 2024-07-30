package store.buzzbook.core.service.order;

import org.springframework.http.HttpHeaders;

public interface OrderStrategy {
	void process(long orderId, String paymentKey, HttpHeaders headers);
	void nonUserProcess(long orderId, HttpHeaders headers);
}
