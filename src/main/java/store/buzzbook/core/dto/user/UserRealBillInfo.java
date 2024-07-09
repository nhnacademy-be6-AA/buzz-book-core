package store.buzzbook.core.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRealBillInfo {
	private int deliveryRate;
	private List<UserRealBillInfoDetail> detailList;
}
