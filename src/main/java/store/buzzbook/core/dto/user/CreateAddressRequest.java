package store.buzzbook.core.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.entity.user.User;

@Builder
public record CreateAddressRequest(
	@NotNull
	String address,
	@NotNull
	String detail,
	@Min(0) @Max(99999)
	@NotNull
	Integer zipcode,
	@NotNull
	String nation,
	@NotNull
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
