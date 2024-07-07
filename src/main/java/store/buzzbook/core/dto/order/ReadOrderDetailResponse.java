package store.buzzbook.core.dto.order;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.buzzbook.core.dto.product.ProductResponse;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ReadOrderDetailResponse {
	private long id;
	private int price;
	private int quantity;
	private boolean wrap;
	private LocalDateTime createdAt;
	private ReadOrderStatusResponse readOrderStatusResponse;
	private ReadWrappingResponse readWrappingResponse;
	private ProductResponse productResponse;
	private LocalDateTime updateAt;
}
