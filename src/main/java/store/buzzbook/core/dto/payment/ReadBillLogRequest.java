package store.buzzbook.core.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.common.util.PageRequestInfo;

@Getter
@NoArgsConstructor
public class ReadBillLogRequest extends PageRequestInfo {
	private String loginId;
}
