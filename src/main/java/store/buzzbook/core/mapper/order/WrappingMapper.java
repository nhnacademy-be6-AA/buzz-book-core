package store.buzzbook.core.mapper.order;

import store.buzzbook.core.dto.order.ReadWrappingResponse;
import store.buzzbook.core.entity.order.Wrapping;

public class WrappingMapper {
	public static ReadWrappingResponse toDto(Wrapping wrapping) {
		return ReadWrappingResponse.builder()
			.id(wrapping.getId())
			.price(wrapping.getPrice())
			.paper(wrapping.getPaper())
			.build();
	}
}
