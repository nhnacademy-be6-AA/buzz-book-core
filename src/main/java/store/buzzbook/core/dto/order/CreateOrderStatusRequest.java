package store.buzzbook.core.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.user.UserInfo;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderStatusRequest {
	private String name;
}
