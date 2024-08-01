package store.buzzbook.core.dto.payment;

import lombok.Getter;

@Getter
public class SimplePayInfo extends PayInfo {
	public SimplePayInfo(String orderId, int price, String paymentKey) {
		super(orderId, price, PayInfo.PayType.간편결제, paymentKey);
	}
}
