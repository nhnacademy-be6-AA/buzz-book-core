package store.buzzbook.core.dto.user;

import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.entity.user.User;

public record CreateAddressRequest(
	String address,
	String detail,
	Integer zipcode,
	String nation,
	String alias
) {

	public Address toAddress(User user) {
		return Address.builder()
			.address(this.address)
			.user(user)
			.alias(this.alias)
			.detail(this.detail)
			.zipcode(this.zipcode)
			.nation(this.nation).build();
	}
}
