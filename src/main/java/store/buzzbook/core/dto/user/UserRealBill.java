package store.buzzbook.core.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRealBill {
	private Long userId;
	private List<UserRealBillInfo> userRealBillInfoList;
}
