package store.buzzbook.core.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import store.buzzbook.core.common.exception.user.DeactivatedUserException;
import store.buzzbook.core.common.exception.user.GradeNotFoundException;
import store.buzzbook.core.common.exception.user.PasswordIncorrectException;
import store.buzzbook.core.common.exception.user.UnEncryptedPasswordException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.common.service.UserProducerService;
import store.buzzbook.core.dto.user.ChangePasswordRequest;
import store.buzzbook.core.dto.user.DeactivateUserRequest;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.point.PointLogRepository;
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
	private PointLogRepository pointLogRepository;
	@Spy
	private User user;

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

		user = convertToUser(registerUserRequest);

		Mockito.lenient().when(gradeRepository.findByName(Mockito.any())).thenReturn(Optional.of(grade));

	}

	@Test
	@DisplayName("유저 생성")
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

		Assertions.assertDoesNotThrow(() -> userService.requestRegister(registerUserRequest));
	}

	@Test
	@DisplayName("이미 있는 id로 시도. 유저 생성 실패")
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
		Assertions.assertTrue(BCrypt.checkpw(registerUserRequest.password(), loginUserResponse.password()));
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

	@Test
	@DisplayName("로그인 성공 처리")
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

	@Test
	@DisplayName("로그인 성공 처리 중 회원 찾기 실패")
	void testSuccessLoginShouldThrowUserNotFoundException() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals("registerUserRequest.loginId()")) {
					return Optional.of(convertToUser(registerUserRequest));
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(UserNotFoundException.class,
			() -> userService.successLogin(registerUserRequest.loginId()));

	}

	@Test
	@DisplayName("로그인 성공 처리 중 등급 찾기 실패")
	void testSuccessLoginShouldThrowGradeNotFoundException() {
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

				if (loginId.equals("registerUserRequest.loginId()")) {
					return Optional.of(grade);
				}
				return Optional.empty();
			});

		Assertions.assertThrowsExactly(GradeNotFoundException.class,
			() -> userService.successLogin(registerUserRequest.loginId()));

	}

	@Test
	@DisplayName("유저 탈퇴 처리 성공")
	void testDeactivateUser() {
		String reason = "테스트";
		DeactivateUserRequest deactivateUserRequest = new DeactivateUserRequest(registerUserRequest.password(), reason);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertDoesNotThrow(() -> userService.deactivate(user.getId(), deactivateUserRequest));
	}

	@Test
	@DisplayName("유저 탈퇴 처리 중 회원 발견 실패")
	void testDeactivateUserShouldThrowUserNotFoundException() {
		String reason = "테스트";
		DeactivateUserRequest deactivateUserRequest = new DeactivateUserRequest(registerUserRequest.password(), reason);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(651651L)) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(UserNotFoundException.class,
			() -> userService.deactivate(user.getId(), deactivateUserRequest));
	}

	@Test
	@DisplayName("유저 탈퇴 처리 중 패스워드 확인 실패")
	void testDeactivateUserShouldThrowPasswordIncorrectException() {
		String reason = "테스트";
		DeactivateUserRequest deactivateUserRequest = new DeactivateUserRequest("registerUserRequest.password()",
			reason);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(PasswordIncorrectException.class,
			() -> userService.deactivate(user.getId(), deactivateUserRequest));
	}

	@Test
	@DisplayName("휴면 계정 활성화")
	void testActivateUser() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(user.getLoginId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertDoesNotThrow(() -> userService.activate(user.getLoginId()));
	}

	@Test
	@DisplayName("휴면 계정 활성화 중 유저 발견 실패")
	void testActivateUserShouldThrowUserNotFoundException() {
		Mockito.when(userRepository.findByLoginId(Mockito.anyString())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals("user.getLoginId()")) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(UserNotFoundException.class, () -> userService.activate(user.getLoginId()));
	}

	@Test
	@DisplayName("계정 수정 성공")
	void testUpdateUserInfo() {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(
			"testName", "00099991234", "asd123@home.mail"
		);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertDoesNotThrow(() -> userService.updateUserInfo(user.getId(), updateUserRequest));
	}

	@Test
	@DisplayName("계정 수정 중 계정 발견 실패")
	void testUpdateUserInfoShouldThrowUserNotFoundException() {
		UpdateUserRequest updateUserRequest = new UpdateUserRequest(
			"testName", "00099991234", "asd123@home.mail"
		);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(123L)) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(UserNotFoundException.class,
			() -> userService.updateUserInfo(user.getId(), updateUserRequest));
	}

	@Test
	@DisplayName("유저 아이디로 유저 정보 얻기")
	void testGetUserInfoByUserId() {

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.findGradeByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(user.getLoginId())) {
					return Optional.of(grade);
				}

				return Optional.empty();
			}
		);

		UserInfo result = userService.getUserInfoByUserId(user.getId());
		Assertions.assertNotNull(result);
		Assertions.assertEquals(grade.getName(), result.grade().getName());
		Assertions.assertEquals(grade.getBenefit(), result.grade().getBenefit());
		Assertions.assertEquals(grade.getStandard(), result.grade().getStandard());
		Assertions.assertEquals(grade.getId(), result.grade().getId());
		Assertions.assertEquals(user.getId(), result.id());
		Assertions.assertEquals(user.getName(), result.name());
		Assertions.assertEquals(user.getLoginId(), result.loginId());
		Assertions.assertEquals(user.getContactNumber(), result.contactNumber());
		Assertions.assertEquals(user.getEmail(), result.email());
		Assertions.assertEquals(user.getBirthday(), result.birthday());

	}

	@Test
	@DisplayName("유저 아이디로 유저 정보 얻는 중 유저 발견 실패")
	void testGetUserInfoByUserIdShouldThrowUserNotFoundException() {

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.findGradeByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals("user.getLoginId()")) {
					return Optional.of(grade);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(GradeNotFoundException.class,
			() -> userService.getUserInfoByUserId(user.getId()));
	}

	@Test
	@DisplayName("유저 아이디로 유저 정보 얻는 중 등급 발견 실패")
	void testGetUserInfoByUserIdShouldThrowGradeNotFoundException() {

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(GradeNotFoundException.class,
			() -> userService.getUserInfoByUserId(user.getId()));
	}

	@Test
	@DisplayName("로그인 아이디로 유저 정보 얻기")
	void testGetUserInfoByLoginId() {

		Mockito.when(userRepository.findByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(user.getLoginId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.findGradeByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(user.getLoginId())) {
					return Optional.of(grade);
				}

				return Optional.empty();
			}
		);

		UserInfo result = userService.getUserInfoByLoginId(user.getLoginId());
		Assertions.assertNotNull(result);
		Assertions.assertEquals(grade.getName(), result.grade().getName());
		Assertions.assertEquals(grade.getBenefit(), result.grade().getBenefit());
		Assertions.assertEquals(grade.getStandard(), result.grade().getStandard());
		Assertions.assertEquals(grade.getId(), result.grade().getId());
		Assertions.assertEquals(user.getId(), result.id());
		Assertions.assertEquals(user.getName(), result.name());
		Assertions.assertEquals(user.getLoginId(), result.loginId());
		Assertions.assertEquals(user.getContactNumber(), result.contactNumber());
		Assertions.assertEquals(user.getEmail(), result.email());
		Assertions.assertEquals(user.getBirthday(), result.birthday());

	}

	@Test
	@DisplayName("로그인 아이디로 유저 정보 얻는 중 유저 발견 실패")
	void testGetUserInfoByLoginIdShouldThrowUserNotFoundException() {

		Mockito.when(userRepository.findByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals("user.getLoginId()")) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(UserNotFoundException.class,
			() -> userService.getUserInfoByLoginId(user.getLoginId()));
	}

	@Test
	@DisplayName("로그인 아이디로 유저 정보 얻는 중 등급 발견 실패")
	void testGetUserInfoByLoginIdShouldThrowGradeNotFoundException() {

		Mockito.when(userRepository.findByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals(user.getLoginId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.findGradeByLoginId(Mockito.any())).thenAnswer(
			invocation -> {
				String loginId = (String)invocation.getArguments()[0];

				if (loginId.equals("user.getLoginId()")) {
					return Optional.of(grade);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(GradeNotFoundException.class,
			() -> userService.getUserInfoByLoginId(user.getLoginId()));
	}

	@Test
	@DisplayName("금월 생일자 리스트")
	void testGetUserInfoByCurrentBirthday() {

		Mockito.when(userRepository.findUsersByBirthdayInCurrentMonth()).thenAnswer(
			invocation -> {
				if (user.getBirthday().getMonth().equals(LocalDate.now().getMonth())) {
					return List.of(user);
				}
				return List.of();
			}
		);

		List<UserInfo> birthdayList = userService.getUserInfoByCurrentBirthday();

		Assertions.assertNotNull(birthdayList);
		Assertions.assertEquals(1, birthdayList.size());

		UserInfo result = birthdayList.getFirst();
		Assertions.assertEquals(user.getBirthday(), result.birthday());
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void testUpdatePassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String confirmedPassword = "zxc1234";
		String newPassword = passwordEncoder.encode(confirmedPassword);

		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
			registerUserRequest.password(), newPassword, confirmedPassword
		);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

		Assertions.assertDoesNotThrow(() -> userService.updatePassword(user.getId(), changePasswordRequest));
	}

	@Test
	@DisplayName("비밀번호 변경 중 암호화 안된 비밀번호")
	void testUpdatePasswordShouldThrowUnEncryptedPasswordException() {
		String confirmedPassword = "zxc1234";

		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
			registerUserRequest.password(), confirmedPassword, confirmedPassword
		);

		Assertions.assertThrowsExactly(
			UnEncryptedPasswordException.class, () -> userService.updatePassword(user.getId(), changePasswordRequest));
	}

	@Test
	@DisplayName("비밀번호 변경 중 유저발견 실패")
	void testUpdatePasswordThrowUserNotFoundException() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String confirmedPassword = "zxc1234";
		String newPassword = passwordEncoder.encode(confirmedPassword);

		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
			registerUserRequest.password(), newPassword, confirmedPassword
		);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(132L)) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(
			UserNotFoundException.class, () -> userService.updatePassword(user.getId(), changePasswordRequest));
	}

	@Test
	@DisplayName("비밀번호 변경 중 비밀번호 확인 실패")
	void testUpdatePasswordThrowPasswordIncorrectExceptionByConfirm() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String confirmedPassword = "zxc1234";
		String newPassword = passwordEncoder.encode(confirmedPassword);

		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
			registerUserRequest.password(), newPassword, "confirmedPassword"
		);

		Assertions.assertThrowsExactly(
			PasswordIncorrectException.class, () -> userService.updatePassword(user.getId(), changePasswordRequest));
	}

	@Test
	@DisplayName("비밀번호 변경 중 비밀번호 인증")
	void testUpdatePasswordThrowPasswordIncorrectExceptionByOldPassword() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String confirmedPassword = "zxc1234";
		String newPassword = passwordEncoder.encode(confirmedPassword);

		ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
			confirmedPassword, newPassword, confirmedPassword
		);

		Mockito.when(userRepository.findById(Mockito.any())).thenAnswer(
			invocation -> {
				Long userId = (Long)invocation.getArguments()[0];

				if (userId.equals(user.getId())) {
					return Optional.of(user);
				}

				return Optional.empty();
			}
		);

		Assertions.assertThrowsExactly(
			PasswordIncorrectException.class, () -> userService.updatePassword(user.getId(), changePasswordRequest));
	}

	private User convertToUser(RegisterUserRequest request) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = passwordEncoder.encode(request.password());

		return User.builder()
			.id(1L)
			.loginId(request.loginId())
			.name(request.name())
			.birthday(request.birthday())
			.createAt(LocalDateTime.now())
			.password(password)
			.email(request.email())
			.modifyAt(null)
			.contactNumber(request.contactNumber())
			.lastLoginAt(null)
			.isAdmin(false).build();
	}

}
