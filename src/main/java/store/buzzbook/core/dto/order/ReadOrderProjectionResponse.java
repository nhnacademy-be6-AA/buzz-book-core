package store.buzzbook.core.dto.order;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Builder
@ToString
public class ReadOrderProjectionResponse {

	private Long id;
	private String orderStr;
	private String loginId;
	private Integer price;
	private String request;
	private String address;
	private String addressDetail;
	private Integer zipcode;
	private LocalDate desiredDeliveryDate;
	private String receiver;
	private ReadOrderDetailProjectionResponse orderDetail;
	private String sender;
	private String receiverContactNumber;
	private String senderContactNumber;

	public ReadOrderProjectionResponse(Long id, String orderStr, String loginId, Integer price,
		String request, String address, String addressDetail, Integer zipcode,
		LocalDate desiredDeliveryDate, String receiver,
		ReadOrderDetailProjectionResponse orderDetail, String sender,
		String receiverContactNumber, String senderContactNumber) {
		this.id = id;
		this.orderStr = orderStr;
		this.loginId = loginId;
		this.price = price;
		this.request = request;
		this.address = address;
		this.addressDetail = addressDetail;
		this.zipcode = zipcode;
		this.desiredDeliveryDate = desiredDeliveryDate;
		this.receiver = receiver;
		this.orderDetail = orderDetail;
		this.sender = sender;
		this.receiverContactNumber = receiverContactNumber;
		this.senderContactNumber = senderContactNumber;
	}
}
