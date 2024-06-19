package store.buzzbook.core.repository.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.point.PointLogRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class PointLogRepositoryTest {

	@Autowired
	private PointLogRepository pointLogRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GradeRepository gradeRepository;

	private PointLog testPointLog;
	private User testUser;
	private Grade testGrade;

	@BeforeEach
	public void setUp() {
		testGrade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		testUser = User.builder()
			.loginId("asd123")
			.name("john doe")
			.grade(testGrade)
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(ZonedDateTime.now())
			.modifyDate(ZonedDateTime.now())
			.createDate(ZonedDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginDate(ZonedDateTime.now())
			.status(UserStatus.ACTIVE)
			.build();

		testPointLog = PointLog.builder()
			.createDate(ZonedDateTime.now())
			.inquiry("book")
			.delta(BigDecimal.valueOf(-10000.00))
			.user(testUser)
			.balance(BigDecimal.valueOf(90000.00))
			.build();

		gradeRepository.save(testGrade);
		userRepository.save(testUser);
		pointLogRepository.save(testPointLog);
	}

	@Test
	@DisplayName("save")
	void save() {
		// given
		PointLog newPointLog = PointLog.builder()
			.createDate(ZonedDateTime.now())
			.inquiry("book")
			.delta(BigDecimal.valueOf(50000.00))
			.user(testUser)
			.balance(BigDecimal.valueOf(95000.00))
			.build();

		pointLogRepository.save(newPointLog);

		// when
		Optional<PointLog> savedPointLog = pointLogRepository.findById(newPointLog.getId());

		// then
		assertTrue(savedPointLog.isPresent());
		assertEquals(newPointLog, savedPointLog.get());
	}

	@Test
	@DisplayName("find by user id")
	void findByUserId() {
		// given
		PointLog newPointLog = PointLog.builder()
			.createDate(ZonedDateTime.now())
			.inquiry("book")
			.delta(BigDecimal.valueOf(50000.00))
			.user(testUser)
			.balance(BigDecimal.valueOf(95000.00))
			.build();

		pointLogRepository.save(newPointLog);

		// when
		List<PointLog> pointLogs = pointLogRepository.findByUserId(testUser.getId());

		// then
		assertEquals(2, pointLogs.size());
	}
}
