package store.buzzbook.core.dto.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.common.util.PageRequestInfo;
import store.buzzbook.core.entity.order.DeliveryPolicy;

@Getter
@NoArgsConstructor
public class ReadOrderRequest extends PageRequestInfo {
	private String loginId;
}
