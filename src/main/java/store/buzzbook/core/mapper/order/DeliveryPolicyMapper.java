package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
import store.buzzbook.core.entity.order.DeliveryPolicy;

public class DeliveryPolicyMapper {
	public static ReadDeliveryPolicyResponse toDto(DeliveryPolicy deliveryPolicy) {
		return ReadDeliveryPolicyResponse.builder()
			.id(deliveryPolicy.getId())
			.name(deliveryPolicy.getName())
			.policyPrice(deliveryPolicy.getPolicyPrice())
			.standardPrice(deliveryPolicy.getStandardPrice())
			.build();
	}
}
