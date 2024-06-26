package store.buzzbook.core.dto.order;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.common.util.PageRequestInfo;
import store.buzzbook.core.entity.order.DeliveryPolicy;

@Getter
@NoArgsConstructor
public class ReadOrderRequest extends PageRequestInfo {
	private String id;
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private ZonedDateTime desiredDeliveryDate;
	private String receiver;
	private DeliveryPolicy deliveryPolicy;
	private String userId;
	private String loginId;
	private boolean isAdmin;
}
