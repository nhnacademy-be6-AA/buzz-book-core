package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.WrappingResponse;
import store.buzzbook.core.entity.order.Wrapping;

public class WrappingMapper {
	public static WrappingResponse toDto(Wrapping wrapping) {
		return WrappingResponse.builder()
			.id(wrapping.getId())
			.price(wrapping.getPrice())
			.paper(wrapping.getPaper())
			.build();
	}
}
