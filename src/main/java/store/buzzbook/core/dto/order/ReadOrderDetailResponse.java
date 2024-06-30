package store.buzzbook.core.dto.order;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	private ReadOrderStatusResponse orderStatus;
	private ReadWrappingResponse wrapping;
	private int productId;
}
