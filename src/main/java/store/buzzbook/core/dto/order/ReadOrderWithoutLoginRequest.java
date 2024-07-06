package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReadOrderWithoutLoginRequest {
	private String orderId;
	private String orderEmail;
}
