package store.buzzbook.core.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateWrappingRequest {
	private int id;
	private String paper;
	private int price;
	private String loginId;
}
