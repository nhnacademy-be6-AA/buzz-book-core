package store.buzzbook.core.dto.order;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReadOrderResponse {
	private long id;
	private int price;
	private String request;
	private String address;
	private String addressDetail;
	private int zipcode;
	private ZonedDateTime desiredDeliveryDate;
	private String receiver;
	private DeliveryPolicyResponse deliveryPolicy;
	private List<ReadOrderDetailResponse> details;
}
