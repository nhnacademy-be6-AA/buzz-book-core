package store.buzzbook.core.dto.review;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import store.buzzbook.core.entity.order.OrderDetail;

@Data
@AllArgsConstructor
@Builder
public class OrderDetailsWithoutReviewResponse {
	private long orderDetailId;
	private LocalDateTime createAt;
	private long productId;
	private String productName;
	private String productThumbnailPath;

	public OrderDetailsWithoutReviewResponse(OrderDetail orderDetail) {
		this.orderDetailId = orderDetail.getId();
		this.createAt = orderDetail.getCreateAt();
		this.productId = orderDetail.getProduct().getId();
		this.productName = orderDetail.getProduct().getProductName();
		this.productThumbnailPath = orderDetail.getProduct().getThumbnailPath();
	}
}
