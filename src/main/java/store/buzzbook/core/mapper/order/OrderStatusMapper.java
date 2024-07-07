package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.entity.order.OrderStatus;

public class OrderStatusMapper {
	public static ReadOrderStatusResponse toDto(OrderStatus orderStatus) {
		return ReadOrderStatusResponse.builder()
			.id(orderStatus.getId())
			.name(orderStatus.getName())
			.updateAt(orderStatus.getUpdateAt())
			.build();
	}
}
