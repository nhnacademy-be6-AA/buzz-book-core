package store.buzzbook.core.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.common.dto.PageRequestInfo;

@Getter
@NoArgsConstructor
public class ReadOrdersRequest extends PageRequestInfo {
	public ReadOrdersRequest(Integer page, Integer size) {
		super(page, size);
	}
}
