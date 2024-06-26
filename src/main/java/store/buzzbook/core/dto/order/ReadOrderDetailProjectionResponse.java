package store.buzzbook.core.dto.order;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@Getter
@ToString
@Setter
public class ReadOrderDetailProjectionResponse {
	private long orderDetailId;
	private int orderDetailPrice;
	private int orderDetailQuantity;
	private String orderDetailWrap;
	private LocalDateTime orderDetailCreatedAt;
	private String orderDetailOrderStatusName;
	private String orderDetailWrappingPaper;
	private String orderDetailProductName;

	public ReadOrderDetailProjectionResponse(long id, int price, int quantity, String wrap, LocalDateTime createdAt,
		String orderStatusName, String wrappingPaper, String productName) {
		this.orderDetailId = id;
		this.orderDetailPrice = price;
		this.orderDetailQuantity = quantity;
		this.orderDetailWrap = wrap;
		this.orderDetailCreatedAt = createdAt;
		this.orderDetailOrderStatusName = orderStatusName;
		this.orderDetailWrappingPaper = wrappingPaper;
		this.orderDetailProductName = productName;
	}
}
