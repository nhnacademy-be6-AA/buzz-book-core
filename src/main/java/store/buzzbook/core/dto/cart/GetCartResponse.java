package store.buzzbook.core.dto.cart;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.cart.CartDetail;

@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class GetCartResponse {
	private Long id;
	private Long userId;
	private String updateDate;
	private List<CartDetail> cartDetailList;
}
