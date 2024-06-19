package store.buzzbook.core.dto.order;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderDetailRequest {
	private int price;
	private int quantity;
	private boolean wrap;
	private ZonedDateTime createDate;
	private long orderStatusId;
	private Long wrappingId;
	private long productId;
	private long orderId;
}
