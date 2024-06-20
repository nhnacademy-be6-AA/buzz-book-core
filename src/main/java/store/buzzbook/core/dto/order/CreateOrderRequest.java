package store.buzzbook.core.dto.order;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.entity.user.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateOrderRequest {
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private ZonedDateTime desiredDeliveryDate;
	private String receiver;
	private int deliveryPolicyId;
	private User user;
	private List<CreateOrderDetailRequest> details;
}
