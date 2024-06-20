package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.OrderStatusResponse;
import store.buzzbook.core.entity.order.OrderStatus;

public class OrderStatusMapper {
	public static OrderStatusResponse toDto(OrderStatus orderStatus) {
		return OrderStatusResponse.builder()
			.id(orderStatus.getId())
			.name(orderStatus.getName())
			.updateDate(orderStatus.getUpdateDate())
			.build();
	}
}
