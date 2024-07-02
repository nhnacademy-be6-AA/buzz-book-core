package store.buzzbook.core.dto.payment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.order.ReadOrderResponse;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ReadPaymentLogResponse {
	private long id;
	private ReadOrderResponse readOrderResponse;
	private String name;
	private int price;
}
