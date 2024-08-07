package store.buzzbook.core.repository.user;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import store.buzzbook.core.common.config.QuerydslConfig;
import store.buzzbook.core.entity.user.Deactivation;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;

@Import(QuerydslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class DeactivationRepositoryTest {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private DeactivationRepository deactivationRepository;
	@Autowired
	private GradeRepository gradeRepository;
	@Autowired
	private UserRepository userRepository;

	private Grade grade;
	private User user;
	private Deactivation deactivation;

	@BeforeEach
	void setUp() {

		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		gradeRepository.save(grade);

		user = User.builder()
			.loginId("dafnjk238")
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

		entityManager.flush();

		deactivation =
			Deactivation
				.builder()
				.user(user)
				.reason("기타")
				.deactivationAt(LocalDateTime.now())
				.build();
	}

	@Test
	@DisplayName("유저 탈퇴 리스트 추가 테스트")
	void testDeactivation() {
		deactivationRepository.save(deactivation);

		Deactivation result = deactivationRepository.findById(user.getId()).orElse(null);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(deactivation.getReason(), result.getReason());
		Assertions.assertEquals(deactivation.getUser().getLoginId(), result.getUser().getLoginId());
		Assertions.assertTrue(deactivationRepository.existsById(deactivation.getId()));
	}
}
