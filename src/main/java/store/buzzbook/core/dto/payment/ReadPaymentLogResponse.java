package store.buzzbook.core.dto.payment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ReadPaymentLogResponse {
	private long id;
	private ReadBillLogResponse readBillLogResponse;

	private String name;
	private int price;
}
