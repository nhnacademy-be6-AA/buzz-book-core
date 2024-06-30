package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadOrderStatusByIdRequest {
	private String loginId;
	private int statusId;
}
