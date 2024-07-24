package store.buzzbook.core.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ReadOrderWithoutLoginRequest {
	private String orderId;
	private String orderEmail;
}
