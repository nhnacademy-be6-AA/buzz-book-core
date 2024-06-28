package store.buzzbook.core.service.user.implement;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.AddressMaxCountException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
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

	@Transactional
	@Override
	public void createAddress(CreateAddressRequest createAddressRequest, long userId) {
		User user = null;
		try {
			user = userRepository.getReferenceById(userId);
		} catch (EntityNotFoundException e) {
			log.debug("회원 주소 추가 중 존재하지 않는 user id의 요청 발생 : {}", userId);
			throw new UserNotFoundException(userId);
		}

		Integer addressCount = addressRepository.countAllByUserId(userId);

		if (addressCount >= 10) {
			log.debug("회원 주소 추가에 실패했습니다. 저장된 주소의 갯수가 최대입니다.");
			throw new AddressMaxCountException(userId);
		}

		Address address = createAddressRequest.toAddress(user);

		addressRepository.save(address);
	}

	@Transactional
	@Override
	public void deleteAddress(Long addressId, Long userId) {
		if (!addressRepository.deleteByIdAndUserId(addressId, userId)) {
			log.debug("잘못된 회원의 주소 삭제 요청입니다. : user : {}, address : {} ", userId, addressId);
			throw new UserNotFoundException(userId);
		}
	}

	@Transactional
	@Override
	public void updateAddress(UpdateAddressRequest updateAddressRequest, long userId) {
		User user = null;

		try {
			user = userRepository.getReferenceById(userId);
		} catch (EntityNotFoundException e) {
			log.debug("회원 주소 수정 중 존재하지 않는 user id의 요청 발생 : {}", userId);
			throw new UserNotFoundException(userId);
		}

		Address address = updateAddressRequest.toAddress(user);
		addressRepository.save(address);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Address> getAddressList(Long userId) {
		if (!userRepository.existsById(userId)) {
			log.debug("존재 하지 않는 회원의 주소 조회 요청입니다. : {}", userId);
			throw new UserNotFoundException(userId);
		}

		return addressRepository.findAllByUserId(userId).orElse(List.of());
	}
}
