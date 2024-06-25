package store.buzzbook.core.dto.order;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.user.UserInfo;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UpdateOrderRequest {
	private long id;
	private UserInfo user;
	private List<UpdateOrderDetailRequest> details;
}
