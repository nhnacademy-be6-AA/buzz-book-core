package store.buzzbook.core.dto.order;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
@ToString
public class ReadOrderProjectionResponse implements Serializable {

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
	private String couponCode;
	private int deliveryRate;
	private String orderEmail;
}
