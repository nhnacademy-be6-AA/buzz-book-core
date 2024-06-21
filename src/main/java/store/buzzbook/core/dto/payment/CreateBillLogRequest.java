package store.buzzbook.core.dto.payment;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.order.Order;
import store.buzzbook.core.entity.payment.BillStatus;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateBillLogRequest {
	private String payment;
	private int price;
	private ZonedDateTime paymentDate;
	private BillStatus status;
	private UUID paymentKey;
	private Order order;
}
