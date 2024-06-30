package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReadDeliveryPolicyRequest {
	private int id;
	private String loginId;
}
