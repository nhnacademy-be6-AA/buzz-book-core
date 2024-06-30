package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadWrappingRequest {
	private int id;
	private String loginId;
}
