package store.buzzbook.core.service.order;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import store.buzzbook.core.dto.payment.PayInfo;

@Component
public class OrderFactory {
	private OrderStrategy orderStrategy;
	private long orderId;
	private PayInfo payInfo;
	private HttpHeaders httpHeaders;

	void setOrderStrategy(OrderStrategy orderStrategy, long orderId, PayInfo payInfo, HttpHeaders httpHeaders) {
		this.orderStrategy = orderStrategy;
		this.orderId = orderId;
		this.payInfo = payInfo;
		this.httpHeaders = httpHeaders;
	}

	void process() {
		orderStrategy.process(orderId, payInfo, httpHeaders);
	}

	void nonUserProcess() {
		orderStrategy.nonUserProcess(orderId);
	}
}
