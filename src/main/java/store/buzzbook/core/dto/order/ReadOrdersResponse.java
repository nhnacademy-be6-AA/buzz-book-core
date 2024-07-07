package store.buzzbook.core.dto.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Builder
@ToString
@AllArgsConstructor
public class ReadOrdersResponse {
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
	@Setter
	private List<ReadOrderDetailProjectionResponse> details;
	private String sender;
	private String receiverContactNumber;
	private String senderContactNumber;
	private String couponCode;
}
