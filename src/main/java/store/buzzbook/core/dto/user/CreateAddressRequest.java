package store.buzzbook.core.dto.user;

public record CreateAddressRequest(
	String address,
	String detail,
	Integer zipcode,
	String nation,
	String alias
) {
}
