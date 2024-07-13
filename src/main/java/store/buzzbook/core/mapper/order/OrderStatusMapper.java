package store.buzzbook.core.mapper.order;

import java.time.format.DateTimeFormatter;

import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
import store.buzzbook.core.entity.order.OrderStatus;

public class OrderStatusMapper {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static ReadOrderStatusResponse toDto(OrderStatus orderStatus) {

		return ReadOrderStatusResponse.builder()
			.id(orderStatus.getId())
			.name(orderStatus.getName())
			.updateAt(orderStatus.getUpdateAt().format(formatter))
			.build();
	}
}
