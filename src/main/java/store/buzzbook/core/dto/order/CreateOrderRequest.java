package store.buzzbook.core.dto.order;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateOrderRequest {
	private String orderStr;
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private String desiredDeliveryDate;
	private String receiver;
	private Integer deliveryPolicyId;
	private String loginId;
	private List<CreateOrderDetailRequest> details;
	private String contactNumber;
	private String email;
	private Integer orderStatusId;
	private String sender;
	private String receiverContactNumber;
}
