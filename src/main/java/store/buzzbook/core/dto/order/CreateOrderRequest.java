package store.buzzbook.core.dto.order;

import java.util.List;

import org.springframework.lang.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateOrderRequest {
	private String orderStr;
	private int price;
	@Nullable
	private String request;
	private String addresses;
	private String address;
	private String addressDetail;
	private int zipcode;
	private String desiredDeliveryDate;
	private String receiver;
	private Integer deliveryPolicyId;
	@Setter
	private String loginId;
	private List<CreateOrderDetailRequest> details;
	private String contactNumber;
	private Integer orderStatusId;
	private String sender;
	private String receiverContactNumber;
	private String orderEmail;
	private Integer myPoint;
	private String couponCode;
	private int deliveryRate;
}
