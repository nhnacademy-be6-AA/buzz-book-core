package store.buzzbook.core.repository.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.entity.user.Address;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class)
@DataJpaTest
class AddressRepositoryTest {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private GradeRepository gradeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AddressRepository addressRepository;

	private Grade grade;
	private User user;
	private Address address;

	@BeforeEach
	void setUp() {

		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		gradeRepository.save(grade);

		user = User.builder()
			.loginId("iojerw398")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now())
			.modifyAt(LocalDateTime.now())
			.createAt(LocalDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginAt(LocalDateTime.now())
			.status(UserStatus.ACTIVE).build();
		userRepository.save(user);

		address = Address.builder()
			.user(user)
			.address("함경북도")
			.alias("남의 집")
			.detail("정은아파트 105동 108호")
			.zipcode(50000)
			.nation("한반도").build();
	}

	@Test
	@DisplayName("유저 주소 추가 및 조회 테스트")
	void testCreateAddressAndFind() {
		addressRepository.save(address);

		Address result = addressRepository.findById(address.getId()).orElse(null);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(address.getAddress(), result.getAddress());
		Assertions.assertEquals(address.getAlias(), result.getAlias());
		Assertions.assertEquals(address.getDetail(), result.getDetail());
		Assertions.assertEquals(address.getZipcode(), result.getZipcode());
		Assertions.assertEquals(address.getNation(), result.getNation());
		Assertions.assertEquals(address.getUser().getLoginId(), result.getUser().getLoginId());
	}

	@Test
	@DisplayName("유저 주소 추가 및 리스트 조회 테스트")
	void testCreateAddressAndFindList() {
		addressRepository.save(address);

		List<Address> result = addressRepository.findAllByUserId(user.getId()).orElse(null);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.size());

		entityManager.clear();

		Address anotherAddress = Address.builder()
			.user(user)
			.address("경기도")
			.alias("우리집")
			.detail("은미아파트 105동 108호")
			.zipcode(50002)
			.nation("남한").build();

		addressRepository.save(anotherAddress);
		result = addressRepository.findAllByUserId(user.getId()).orElse(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(2, result.size());
	}

	@Test
	@DisplayName("주소 삭제 테스트")
	void testDeleteAddressAndFind() {
		addressRepository.save(address);
		Address anotherAddress = Address.builder()
			.user(user)
			.address("경기도")
			.alias("우리집")
			.detail("은미아파트 105동 108호")
			.zipcode(50002)
			.nation("남한").build();

		addressRepository.save(anotherAddress);
		List<Address> result = addressRepository.findAllByUserId(user.getId()).orElse(null);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(2, result.size());

		addressRepository.deleteById(address.getId());
		result = addressRepository.findAllByUserId(user.getId()).orElse(null);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(1, result.size());
	}
}
