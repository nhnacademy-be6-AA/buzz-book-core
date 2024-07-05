package store.buzzbook.core.repository.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(QuerydslConfig.class)
@Slf4j
@Disabled
class UserRepositoryTest {
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
			.id(1L)
			.loginId("testid00000000")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now())
			.modifyAt(LocalDateTime.now())
			.createAt(LocalDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginAt(LocalDateTime.now())
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

}
