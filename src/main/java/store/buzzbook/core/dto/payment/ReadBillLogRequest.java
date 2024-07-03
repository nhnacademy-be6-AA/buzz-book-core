package store.buzzbook.core.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadBillLogRequest {
	private String orderId;
}
