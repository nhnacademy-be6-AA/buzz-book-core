package store.buzzbook.core.dto.order;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.user.User;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UpdateOrderRequest {
	private long id;
	private User user;
	private List<UpdateOrderDetailRequest> details;
}
