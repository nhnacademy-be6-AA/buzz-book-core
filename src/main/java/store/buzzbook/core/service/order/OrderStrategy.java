package store.buzzbook.core.service.order;

import org.springframework.http.HttpHeaders;

import store.buzzbook.core.dto.payment.PayInfo;

public interface OrderStrategy {
	void process(long orderId, PayInfo payInfo, HttpHeaders headers);
	void nonUserProcess(long orderId);
}
