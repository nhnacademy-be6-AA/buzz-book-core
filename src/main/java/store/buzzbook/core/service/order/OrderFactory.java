package store.buzzbook.core.service.order;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class OrderFactory {
	private OrderStrategy orderStrategy;
	private long orderId;
	private String paymentKey;
	private HttpHeaders httpHeaders;

	void setOrderStrategy(OrderStrategy orderStrategy, long orderId, String paymentKey, HttpHeaders httpHeaders) {
		this.orderStrategy = orderStrategy;
		this.orderId = orderId;
		this.paymentKey = paymentKey;
		this.httpHeaders = httpHeaders;
	}

	void process() {
		orderStrategy.process(orderId, paymentKey, httpHeaders);
	}

	void nonUserProcess() {
		orderStrategy.nonUserProcess(orderId);
	}
}
