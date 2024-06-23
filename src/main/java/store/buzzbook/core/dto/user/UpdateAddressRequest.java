package store.buzzbook.core.dto.user;

import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.entity.user.User;

public record UpdateAddressRequest(Long id,
								   String address,
								   String detail,
								   Integer zipcode,
								   String nation,
								   String alias) {
	public Address toAddress(User user) {
		return Address.builder()
			.id(this.id)
			.nation(this.nation)
			.zipcode(this.zipcode)
			.detail(this.detail)
			.alias(this.alias)
			.address(this.address)
			.user(user).build();
	}
}
