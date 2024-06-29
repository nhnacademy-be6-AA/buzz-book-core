package store.buzzbook.core.dto.order;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Builder
@ToString
@AllArgsConstructor
public class ReadOrderWithoutDetailsProjectionResponse {
	private Long orderId;
	private String orderStr;
	private String loginId;
	private Integer price;
	private String request;
	private String address;
	private String addressDetail;
	private Integer zipcode;
	private LocalDate desiredDeliveryDate;
	private String receiver;
	private String sender;
	private String receiverContactNumber;
	private String senderContactNumber;
}
