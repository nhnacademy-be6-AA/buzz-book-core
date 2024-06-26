package store.buzzbook.core.dto.cart;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetCartResponse {
	@NotNull
	private Long id;
	private Long userId;
	private List<CartDetailResponse> cartDetailList;

}
