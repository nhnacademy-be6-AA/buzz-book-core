package store.buzzbook.core.dto.order;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderDetailRequest {
	private int price;
	private int quantity;
	private boolean wrap;
	private String createDate;
	private int orderStatusId;
	private Integer wrappingId;
	private int productId;
	@Setter
	private long orderId;
}
