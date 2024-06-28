package store.buzzbook.core.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.point.PointLogRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Import(QuerydslConfig.class)
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
			.loginId("ewqur32847")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now())
			.modifyAt(LocalDateTime.now())
			.createAt(LocalDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginAt(LocalDateTime.now())
			.status(UserStatus.ACTIVE)
			.build();

		testPointLog = PointLog.builder()
			.createdAt(LocalDateTime.now())
			.inquiry("book")
			.delta(-10000)
			.user(testUser)
			.balance(90000)
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
			.createdAt(LocalDateTime.now())
			.inquiry("book")
			.delta(50000)
			.user(testUser)
			.balance(95000)
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
			.createdAt(LocalDateTime.now())
			.inquiry("book")
			.delta(50000)
			.user(testUser)
			.balance(95000)
			.build();

		pointLogRepository.save(newPointLog);

		// when
		List<PointLog> pointLogs = pointLogRepository.findByUserId(testUser.getId());

		// then
		assertEquals(2, pointLogs.size());
	}
}
