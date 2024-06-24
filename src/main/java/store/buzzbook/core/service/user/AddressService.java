package store.buzzbook.core.service.user;

import java.util.List;

import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
import store.buzzbook.core.entity.user.Address;

public interface AddressService {
	void createAddress(CreateAddressRequest createAddressRequest, long userId);

	void deleteAddress(Long addressId, Long userId);

	void updateAddress(UpdateAddressRequest updateAddressRequest, long userId);

	List<Address> getAddressList(Long userId);

}
