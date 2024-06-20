package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.DeliveryPolicyResponse;
import store.buzzbook.core.entity.order.DeliveryPolicy;

public class DeliveryPolicyMapper {
	public static DeliveryPolicyResponse toDto(DeliveryPolicy deliveryPolicy) {
		return DeliveryPolicyResponse.builder()
			.id(deliveryPolicy.getId())
			.name(deliveryPolicy.getName())
			.policyPrice(deliveryPolicy.getPolicyPrice())
			.standardPrice(deliveryPolicy.getStandardPrice())
			.build();
	}
}
