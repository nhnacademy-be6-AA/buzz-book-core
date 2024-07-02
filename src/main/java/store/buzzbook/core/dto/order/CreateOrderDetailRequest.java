package store.buzzbook.core.dto.order;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderDetailRequest {
	@Setter
	private int price;
	private int quantity;
	private boolean wrap;
	private LocalDateTime createDate;
	private int orderStatusId;
	private Integer wrappingId;
	@Setter
	private long orderId;
	private int productId;
	private String productName;
	private String thumbnailPath;
	private String couponCode;
}
