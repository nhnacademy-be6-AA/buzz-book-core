package store.buzzbook.core.dto.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReadOrderWithBillLogsResponse {
	private Long orderId;
	private String orderStr;
	private String loginId;
	private Integer orderPrice;
	private String request;
	private String address;
	private String addressDetail;
	private Integer zipcode;
	private LocalDate desiredDeliveryDate;
	private String receiver;
	private String sender;
	private String receiverContactNumber;
	private String senderContactNumber;
	private String couponCode;
	private String orderEmail;
	private Integer deliveryRate;

	private List<ReadBillLogWithoutOrderResponse> billlogs;
}
