package store.buzzbook.core.service.user.implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.AddressRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.AddressService;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

	private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;

	@Override
	public void createAddress(CreateAddressRequest createAddressRequest, long userId) {
		User user = null;
		try {
			user = userRepository.getReferenceById(userId);
		} catch (EntityNotFoundException e) {
			log.warn("유저 주소 추가 중 존재하지 않는 user id의 요청 발생 : {}", userId);
			throw new UserNotFoundException(userId);
		}

		Address address = createAddressRequest.toAddress(user);

		addressRepository.save(address);
	}

	@Override
	public void deleteAddress(long addressId, long userId) {
		if (!addressRepository.deleteByIdAndUserId(addressId, userId)) {
			log.warn("잘못된 유저의 주소 삭제 요청입니다. : user : {}, address : {} ", userId, addressId);
			throw new UserNotFoundException(userId);
		}

	}
}
