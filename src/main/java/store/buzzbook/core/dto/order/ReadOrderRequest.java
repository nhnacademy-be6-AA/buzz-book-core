package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.common.util.PageRequestInfo;

@Getter
@NoArgsConstructor
public class ReadOrderRequest extends PageRequestInfo {
	private int price;
	private String loginId;
	private String deliveryPolicyId;
	private boolean isAdmin;
}
