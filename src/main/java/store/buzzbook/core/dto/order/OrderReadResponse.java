package store.buzzbook.core.dto.order;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.order.DeliveryPolicy;
import store.buzzbook.core.entity.payment.BillLog;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class OrderReadResponse {
	private long id;
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private ZonedDateTime desiredDeliveryDate;
	private String receiver;
	private DeliveryPolicy deliveryPolicy;
	private List<OrderDetailResponse> details;
}
