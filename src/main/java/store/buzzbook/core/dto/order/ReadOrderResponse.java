package store.buzzbook.core.dto.order;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class ReadOrderResponse {
	private long id;
	private String orderStr;
	private String loginId;
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private String desiredDeliveryDate;
	private String receiver;
	private List<ReadOrderDetailResponse> details;
	private String sender;
	private String receiverContactNumber;
	private String senderContactNumber;
	private String orderEmail;
	private String couponCode;
	private int deliveryRate;
}
