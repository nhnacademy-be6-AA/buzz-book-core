package store.buzzbook.core.dto.order;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.user.UserInfo;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateOrderRequest {
	private String orderId;
	private String orderStatusName;
}
