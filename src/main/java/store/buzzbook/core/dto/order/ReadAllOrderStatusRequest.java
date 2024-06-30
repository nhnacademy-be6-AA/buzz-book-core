package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadAllOrderStatusRequest {
	private String loginId;
}
