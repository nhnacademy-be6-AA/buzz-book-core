package store.buzzbook.core.dto.payment;

import lombok.Getter;

@Getter
public class PointPayInfo extends PayInfo {
	public PointPayInfo(String orderId, int price, String paymentKey) {
		super(orderId, price, PayType.POINT, paymentKey);
	}
}
