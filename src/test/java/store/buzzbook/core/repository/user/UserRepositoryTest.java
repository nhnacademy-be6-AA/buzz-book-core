package store.buzzbook.core.repository.user;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(QuerydslConfig.class)
class UserRepositoryTest {
	private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);
	@Autowired
	private EntityManager em;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GradeRepository gradeRepository;
	private Grade grade;
	private User user;

	@BeforeEach
	public void setUp() {
		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		gradeRepository.save(grade);

		user = User.builder()
			.loginId("testid00000000")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(ZonedDateTime.now().toLocalDate())
			.modifyAt(ZonedDateTime.now())
			.createAt(ZonedDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginAt(ZonedDateTime.now())
			.isAdmin(false)
			.status(UserStatus.ACTIVE).build();

		userRepository.save(user);
	}

	@Test
	@DisplayName("유저 생성 및 조회와 등급")
	void testCreateUser() {

		Grade resultGrade = gradeRepository.findById(grade.getId()).orElse(null);

		Assertions.assertNotNull(resultGrade);
		Assertions.assertEquals(grade.getId(), resultGrade.getId());
		Assertions.assertEquals(grade.getName(), resultGrade.getName());
		Assertions.assertEquals(grade.getStandard(), resultGrade.getStandard());
		Assertions.assertEquals(grade.getBenefit(), resultGrade.getBenefit());

		userRepository.save(user);
		User result = userRepository.findById(user.getId()).orElse(null);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(user.getId(), result.getId());
		Assertions.assertEquals(user.getLoginId(), result.getLoginId());
		Assertions.assertEquals(user.getPassword(), result.getPassword());
		Assertions.assertEquals(user.getStatus(), result.getStatus());
		Assertions.assertEquals(user.getContactNumber(), result.getContactNumber());
		Assertions.assertEquals(user.getName(), result.getName());
	}

	@Test
	void testUpdateLoginDate() {
		userRepository.updateLoginDate(user.getLoginId());

		User updatedUser = userRepository.findById(user.getId()).orElse(null);

		Assertions.assertNotNull(updatedUser);
		Assertions.assertEquals(user.getLoginId(), updatedUser.getLoginId());
		log.info("last login date: {}", user.getLastLoginAt());
	}

	@Test
	void testUpdateStatus() {
		User savedUser = userRepository.findById(user.getId()).orElse(null);

		Assertions.assertNotNull(savedUser);
		Assertions.assertEquals(UserStatus.ACTIVE, savedUser.getStatus());

		em.flush();
		em.clear();

		Assertions.assertTrue(userRepository.updateStatus(user.getLoginId(), UserStatus.DORMANT));

		User updatedUser = userRepository.findById(user.getId()).orElse(null);
		Assertions.assertNotNull(updatedUser);
		Assertions.assertEquals(UserStatus.DORMANT, updatedUser.getStatus());

	}
}
