package store.buzzbook.core.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import store.buzzbook.core.common.exception.user.DeactivatedUserException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.common.service.UserProducerService;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.DeactivationRepository;
import store.buzzbook.core.repository.user.GradeLogRepository;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.implement.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private GradeRepository gradeRepository;
	@Mock
	private DeactivationRepository deactivationRepository;
	@Mock
	private GradeLogRepository gradeLogRepository;
	@Mock
	private UserProducerService userProducerService;
	@Spy
	User user;

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
			.loginId("ijodfs328")
			.birthday(LocalDate.now())
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

		Mockito.when(gradeLogRepository.save(Mockito.any()))
			.thenReturn(null);

		Mockito.lenient().when(userRepository.findGradeByLoginId(Mockito.anyString()))
			.thenReturn(Optional.of(grade));

		Mockito.doNothing().when(userProducerService).sendWelcomeCouponRequest(Mockito.any());

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

	@Test
	void testGetUserInfoByLoginId() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(registerUserRequest.loginId())) {
					return Optional.of(convertToUser(registerUserRequest));
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.findGradeByLoginId(Mockito.anyString()))
			.thenReturn(Optional.of(grade));

		UserInfo responseInfo = userService.getUserInfoByLoginId(registerUserRequest.loginId());
		Assertions.assertNotNull(responseInfo);
		Assertions.assertEquals(registerUserRequest.loginId(), responseInfo.loginId());
		Assertions.assertEquals(registerUserRequest.name(), responseInfo.name());
		Assertions.assertEquals(registerUserRequest.email(), responseInfo.email());
		Assertions.assertEquals(registerUserRequest.contactNumber(), responseInfo.contactNumber());
		Assertions.assertEquals(registerUserRequest.birthday(), responseInfo.birthday());

	}

	@Test
	void testRequestLoginShouldOk() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString()))
			.thenAnswer(invocation -> {
				String loginId = (String)invocation.getArguments()[0];
				if (loginId.equals(registerUserRequest.loginId())) {
					return Optional.of(convertToUser(registerUserRequest));
				}

				return Optional.empty();
			});

		Mockito.when(deactivationRepository.existsById(Mockito.any()))
			.thenReturn(false);

		LoginUserResponse loginUserResponse = userService.requestLogin(registerUserRequest.loginId());
		Assertions.assertNotNull(loginUserResponse);
		Assertions.assertEquals(registerUserRequest.loginId(), loginUserResponse.loginId());
		Assertions.assertEquals(registerUserRequest.password(), loginUserResponse.password());
	}

	@Test
	@DisplayName("등록된 아이디가 없는 로그인 시도")
	void testRequestLoginShouldThrowUserNotFoundException() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString()))
			.thenAnswer(invocation -> {
				String loginId = (String)invocation.getArguments()[0];
				if (loginId.equals("UNKNOWN_ID")) {
					return Optional.of(convertToUser(registerUserRequest));
				}

				return Optional.empty();
			});

		Assertions.assertThrowsExactly(UserNotFoundException.class,
			() -> userService.requestLogin(registerUserRequest.loginId()));

	}

	@Test
	@DisplayName("탈퇴한 유저 로그인 시도")
	void testRequestLoginShouldThrowDeactivateUserException() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString()))
			.thenAnswer(invocation -> {
				String loginId = (String)invocation.getArguments()[0];
				if (loginId.equals(registerUserRequest.loginId())) {
					return Optional.of(convertToUser(registerUserRequest));
				}

				return Optional.empty();
			});

		Mockito.when(deactivationRepository.existsById(Mockito.any()))
			.thenReturn(true);

		Assertions.assertThrowsExactly(DeactivatedUserException.class,
			() -> userService.requestLogin(registerUserRequest.loginId()));

	}

	@Disabled("mock 주입 중")
	@Test
	void testSuccessLogin() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(registerUserRequest.loginId())) {
					return Optional.of(convertToUser(registerUserRequest));
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.findGradeByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(registerUserRequest.loginId())) {
					return Optional.of(grade);
				}
				return Optional.empty();
			});

		Mockito.when(user.toUserInfo(Mockito.any())).thenReturn(registerUserRequest.toUser().toUserInfo(grade));

		Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(convertToUser(registerUserRequest));

		Mockito.when(userRepository.findGradeByLoginId(Mockito.anyString()))
			.thenReturn(Optional.of(grade));

		UserInfo responseInfo = userService.successLogin(registerUserRequest.loginId());

		Assertions.assertNotNull(responseInfo);
		Assertions.assertEquals(registerUserRequest.loginId(), responseInfo.loginId());
		Assertions.assertEquals(registerUserRequest.name(), responseInfo.name());
		Assertions.assertEquals(registerUserRequest.email(), responseInfo.email());
		Assertions.assertEquals(registerUserRequest.contactNumber(), responseInfo.contactNumber());
		Assertions.assertEquals(registerUserRequest.birthday(), responseInfo.birthday());

	}

	private User convertToUser(RegisterUserRequest request) {

		return User.builder()
			.id(1L)
			.loginId(request.loginId())
			.name(request.name())
			.birthday(request.birthday())
			.createAt(LocalDateTime.now())
			.password(request.password())
			.email(request.email())
			.modifyAt(null)
			.contactNumber(request.contactNumber())
			.lastLoginAt(null)
			.isAdmin(false).build();
	}

}
