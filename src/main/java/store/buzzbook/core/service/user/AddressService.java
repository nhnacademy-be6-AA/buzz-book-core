package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.user.CreateAddressRequest;

public interface AddressService {
	void createAddress(CreateAddressRequest createAddressRequest, long userId);

	void deleteAddress(long addressId, long userId);
}
