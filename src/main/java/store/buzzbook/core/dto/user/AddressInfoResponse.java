package store.buzzbook.core.dto.user;

import lombok.Builder;

@Builder
public record AddressInfoResponse(
	long id,
	String address,
	String detail,
	int zipcode,
	String nation,
	String alias
) {

}
