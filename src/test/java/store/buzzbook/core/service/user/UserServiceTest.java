package store.buzzbook.core.service.user;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.implement.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private GradeRepository gradeRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private RegisterUserRequest registerUserRequest;
	private Grade grade;

	@BeforeEach
	void setUp() {
		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		registerUserRequest = RegisterUserRequest.builder()
			.name("test")
			.email("asd123@nhn.com")
			.contactNumber("010-0000-1111")
			.loginId("asd123")
			.birthday(ZonedDateTimeParser.toStringDate(ZonedDateTime.now()))
			.password("328u1u90uiodhiosdafhioufo82^&%6712jbsja")
			.build();

		Mockito.lenient().when(gradeRepository.findByName(Mockito.any())).thenReturn(Optional.of(grade));

	}

	@Test
	void testCreateUser() {
		Mockito.when(userRepository.existsByLoginId(Mockito.anyString()))
			.thenAnswer(invocation -> {
				String duplicatedLoginId = "duplicatedLoginId";

				String requestId = (String)invocation.getArguments()[0];
				return requestId.equals(duplicatedLoginId);
			});

		Mockito.lenient().when(userRepository.save(Mockito.any(User.class)))
			.thenReturn(convertToUser(registerUserRequest));

		RegisterUserResponse response = userService.requestRegister(registerUserRequest);

		Assertions.assertNotNull(response);
		Assertions.assertEquals(registerUserRequest.loginId(), response.loginId());
		Assertions.assertEquals(HttpStatus.OK.value(), response.status());
		Assertions.assertEquals(registerUserRequest.name(), response.name());
	}

	@Test
	void testCreateUserShouldOccurredException() {
		Mockito.when(userRepository.existsByLoginId(Mockito.anyString()))
			.thenAnswer(invocation -> {
				String duplicatedLoginId = registerUserRequest.loginId();

				String requestId = (String)invocation.getArguments()[0];
				return requestId.equals(duplicatedLoginId);
			});

		Mockito.lenient().when(userRepository.save(Mockito.any(User.class)))
			.thenReturn(convertToUser(registerUserRequest));

		Assertions.assertThrowsExactly(UserAlreadyExistsException.class,
			() -> userService.requestRegister(registerUserRequest));

	}

	private User convertToUser(RegisterUserRequest request) {

		return User.builder()
			.loginId(request.loginId())
			.name(request.name())
			.birthday(ZonedDateTimeParser.toDate(request.birthday()))
			.createDate(ZonedDateTime.now())
			.grade(grade)
			.email(request.email())
			.modifyDate(null)
			.contactNumber(request.contactNumber())
			.lastLoginDate(null)
			.isAdmin(false).build();
	}
}
