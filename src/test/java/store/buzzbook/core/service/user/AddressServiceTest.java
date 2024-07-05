package store.buzzbook.core.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.user.AddressRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.implement.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
@Disabled
class AddressServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private AddressRepository addressRepository;

	@InjectMocks
	private AddressServiceImpl addressService;

	CreateAddressRequest createAddressRequest;

	private User user;
	private Grade grade;
	private Long notExistId = -1L;

	@BeforeEach
	public void setUp() {
		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		user = User.builder()
			.id(100L)
			.password("ds4f6a4fd6@#8rfe84r64er")
			.createAt(LocalDateTime.now())
			.birthday(LocalDate.now())
			.status(UserStatus.ACTIVE)
			.loginId("asd123")
			.name("테스트")
			.email("qqqq132@test.com")
			.contactNumber("01097946333").build();

		createAddressRequest =
			new CreateAddressRequest("광주광역시",
				"일반아파트",
				12345,
				"한국",
				"우리집");
	}

	@Test
	void createAddress() {
		Mockito.lenient().when(userRepository.getReferenceById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long id = invocation.getArgument(0);
				if (id.equals(user.getId())) {
					return user;
				}
				throw new EntityNotFoundException();
			});

		Mockito.lenient().when(addressRepository.save(Mockito.any()))
			.thenReturn(createAddressRequest.toAddress(user));

		Assertions.assertDoesNotThrow(
			() -> addressService.createAddress(createAddressRequest, user.getId())
		);

	}

	@Test
	@DisplayName("존재하지 않는 id로 주소 저장 시도")
	void createAddressShouldOccurrException() {
		Mockito.lenient().when(userRepository.getReferenceById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long id = invocation.getArgument(0);
				if (id.equals(notExistId)) {
					return user;
				}
				throw new EntityNotFoundException();
			});

		Mockito.lenient().when(addressRepository.save(Mockito.any()))
			.thenReturn(createAddressRequest.toAddress(user));

		Assertions.assertThrowsExactly(
			UserNotFoundException.class,
			() -> addressService.createAddress(createAddressRequest, user.getId())
		);

	}

	@Test
	@DisplayName("주소 삭제 시도 성공")
	void deleteAddressShouldOk() {
		Long existAddressId = 100L;

		Mockito.when(addressRepository.deleteByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long addressId = invocation.getArgument(0);
				Long userId = invocation.getArgument(1);

				return addressId.equals(existAddressId) && userId.equals(user.getId());
			});

		Assertions.assertDoesNotThrow(() -> addressService.deleteAddress(existAddressId, user.getId()));

	}

	@Test
	@DisplayName("잘못된 회원 혹은 주소의 주소 삭제 시도")
	void deleteAddressShouldOccurrException() {
		Long existAddressId = 100L;

		Mockito.when(addressRepository.deleteByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long addressId = invocation.getArgument(0);
				Long userId = invocation.getArgument(1);

				return addressId.equals(notExistId) && userId.equals(user.getId());
			});

		Assertions.assertThrowsExactly(
			UserNotFoundException.class,
			() -> addressService.deleteAddress(existAddressId, user.getId()));

	}

	@Test
	@DisplayName("주소 수정 요청 성공")
	void updateAddressShouldOk() {
		UpdateAddressRequest updateAddressRequest =
			new UpdateAddressRequest(
				100L,
				createAddressRequest.address(),
				createAddressRequest.detail(),
				createAddressRequest.zipcode(),
				createAddressRequest.nation(),
				createAddressRequest.alias());

		Mockito.when(userRepository.getReferenceById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				if (userId.equals(user.getId())) {
					return user;
				}
				throw new EntityNotFoundException();
			});

		Mockito.lenient().when(addressRepository.save(Mockito.any()))
			.thenReturn(createAddressRequest.toAddress(user));

		Assertions.assertDoesNotThrow(() -> addressService.updateAddress(updateAddressRequest, user.getId()));
	}

	@Test
	@DisplayName("주소 수정 요청 실패")
	void updateAddressShouldOccurrException() {
		UpdateAddressRequest updateAddressRequest =
			new UpdateAddressRequest(
				100L,
				createAddressRequest.address(),
				createAddressRequest.detail(),
				createAddressRequest.zipcode(),
				createAddressRequest.nation(),
				createAddressRequest.alias());

		Mockito.when(userRepository.getReferenceById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				if (userId.equals(notExistId)) {
					return user;
				}
				throw new EntityNotFoundException();
			});

		Mockito.lenient().when(addressRepository.save(Mockito.any()))
			.thenReturn(createAddressRequest.toAddress(user));

		Assertions.assertThrowsExactly(UserNotFoundException.class
			, () -> addressService.updateAddress(updateAddressRequest, user.getId()));
	}

	@Disabled
	@Test
	@DisplayName("주소 목록 조회 성공")
	void testGetAddressListShouldOk() {
		Mockito.when(userRepository.existsById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				return userId.equals(user.getId());
			});

		Mockito.when(addressRepository.findAllByUserId(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				if (userId.equals(user.getId())) {
					return Optional.of(List.of(createAddressRequest.toAddress(user)));
				}
				return Optional.empty();
			});
		//
		// List<Address> addressList = addressService.getAddressList(user.getId());
		//
		// Assertions.assertEquals(1, addressList.size());
		// Assertions.assertEquals(user.getId(), addressList.getFirst().getUser().getId());
		// Assertions.assertEquals(user.getLoginId(), addressList.getFirst().getUser().getLoginId());
		// Assertions.assertEquals(user.getPassword(), addressList.getFirst().getUser().getPassword());
		// Assertions.assertEquals(user.getBirthday(), addressList.getFirst().getUser().getBirthday());
		// Assertions.assertEquals(user.getStatus(), addressList.getFirst().getUser().getStatus());
		// Assertions.assertEquals(user.getName(), addressList.getFirst().getUser().getName());
		// Assertions.assertEquals(user.getEmail(), addressList.getFirst().getUser().getEmail());
		// Assertions.assertEquals(user.getContactNumber(), addressList.getFirst().getUser().getContactNumber());
		// Assertions.assertEquals(createAddressRequest.address(), addressList.getFirst().getAddress());
		// Assertions.assertEquals(createAddressRequest.detail(), addressList.getFirst().getDetail());
		// Assertions.assertEquals(createAddressRequest.zipcode(), addressList.getFirst().getZipcode());
		// Assertions.assertEquals(createAddressRequest.nation(), addressList.getFirst().getNation());
		// Assertions.assertEquals(createAddressRequest.alias(), addressList.getFirst().getAlias());

	}

	@Test
	@DisplayName("주소 목록 조회 성공")
	void testGetAddressListShouldOccurrException() {
		Mockito.when(userRepository.existsById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				return userId.equals(notExistId);
			});

		Assertions.assertThrowsExactly(
			UserNotFoundException.class,
			() -> addressService.getAddressList(user.getId()));

	}

	@Disabled
	@Test
	@DisplayName("주소 목록 조회 성공")
	void testGetAddressListShouldReturnEmpty() {
		Mockito.when(userRepository.existsById(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				return userId.equals(user.getId());
			});

		Mockito.when(addressRepository.findAllByUserId(Mockito.anyLong()))
			.thenAnswer(invocation -> {
				Long userId = invocation.getArgument(0);
				if (userId.equals(notExistId)) {
					return Optional.of(List.of(createAddressRequest.toAddress(user)));
				}
				return Optional.empty();
			});

		// List<Address> addressList = addressService.getAddressList(user.getId());
		//
		// Assertions.assertEquals(0, addressList.size());

	}

}
