package store.buzzbook.core.dto.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReadDeliveryPolicyResponse {
	private int id;
	private String name;
	private int standardPrice;
	private int policyPrice;
	private boolean deleted;
}
