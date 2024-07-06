package store.buzzbook.core.dto.order;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.buzzbook.core.common.util.PageRequestInfo;
import store.buzzbook.core.dto.user.UserInfo;

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
	private LocalDate desiredDeliveryDate;
	private String receiver;
	private List<ReadOrderDetailResponse> details;
	private String sender;
	private String receiverContactNumber;
	private String senderContactNumber;
	private String orderEmail;
}
