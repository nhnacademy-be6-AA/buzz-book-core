package store.buzzbook.core.repository.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import store.buzzbook.account.domain.user.Grade;
import store.buzzbook.account.domain.user.User;
import store.buzzbook.account.domain.user.UserStatus;
import store.buzzbook.account.repository.account.GradeRepository;
import store.buzzbook.account.repository.account.UserRepository;

import java.time.ZonedDateTime;

@DataJpaTest
class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GradeRepository gradeRepository;
	private Grade grade;

	@BeforeEach
	public void setUp() {
		grade = Grade.builder()
			.benefit(2.5)
			.name("플래티넘")
			.standard(200000)
			.build();


	}

	@Test
	@DisplayName("유저 생성 및 조회와 등급")
	void testCreateUser(){
		gradeRepository.save(grade);
		Grade resultGrade = gradeRepository.findById(grade.getId()).orElse(null);

		Assertions.assertNotNull(resultGrade);
		Assertions.assertEquals(grade.getId(), resultGrade.getId());
		Assertions.assertEquals(grade.getName(), resultGrade.getName());
		Assertions.assertEquals(grade.getStandard(), resultGrade.getStandard());
		Assertions.assertEquals(grade.getBenefit(), resultGrade.getBenefit());


		User user = User.builder()
				.loginId("asd123")
				.name("john doe")
				.grade(grade)
				.email("email123@nhn.com")
				.contactNumber("010-0000-1111")
				.birthday(ZonedDateTime.now())
				.modifyDate(ZonedDateTime.now())
			.createDate(ZonedDateTime.now())
			.password("encrytedsolongpassword123345")
			.lastLoginDate(ZonedDateTime.now())
				.isAdmin(false)
			.status(UserStatus.ACTIVE).build();


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
