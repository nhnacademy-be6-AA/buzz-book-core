package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeleteDeliveryPolicyRequest {
	private int id;
	private String loginId;
}
