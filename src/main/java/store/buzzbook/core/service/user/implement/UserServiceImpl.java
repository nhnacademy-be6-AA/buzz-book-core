package store.buzzbook.core.service.user.implement;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.user.DeactivateUserException;
import store.buzzbook.core.common.exception.user.GradeNotFoundException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.common.util.ZonedDateTimeParser;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.Deactivation;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.user.DeactivationRepository;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final GradeRepository gradeRepository;
	private final DeactivationRepository deactivationRepository;

	private final String SUCCESS_REGISTER = "회원가입 성공";

	@Override
	public LoginUserResponse requestLogin(String loginId) {
		User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId));

		boolean isDeactivate = deactivationRepository.existsByUserId(user.getId());

		if (isDeactivate) {
			log.warn("로그인 실패 : 탈퇴한 유저의 아이디({})입니다.", user.getId());
			throw new DeactivateUserException(loginId);
		}

		return LoginUserResponse.convertFrom(user);
	}

	@Override
	public UserInfo successLogin(Long id) {
		//todo 뭔가 보안을 위한 토큰 같은게 필요할까요?
		// boolean isUpdated = userRepository.updateLastLoginDateById(id,ZonedDateTime.now());
		// if (!isUpdated) {
		// 	//한번 더 시도
		// 	isUpdated = userRepository.updateLastLoginDateById(id,ZonedDateTime.now());
		// }

		// if (!isUpdated) {
		// 	log.warn("로그인 성공 처리 실패 : 알 수 없는 오류");
		// 	throw new UnknownUserException("로그인 성공처리 업데이트 실패");
		// }

		User user = userRepository.findById(id).orElseThrow(
			() -> new UserNotFoundException(String.format("long id %d", id)));

		return UserInfo.builder()
			.loginId(user.getLoginId())
			.id(user.getId())
			.name(user.getName())
			.email(user.getEmail())
			.grade(user.getGrade())
			.birthday(user.getBirthday())
			.contactNumber(user.getContactNumber())
			.isAdmin(user.isAdmin()).build();

	}

	@Override
	public RegisterUserResponse requestRegister(RegisterUserRequest registerUserRequest) {
		String loginId = registerUserRequest.loginId();

		if (userRepository.existsByLoginId(loginId)) {
			log.warn("유저 아이디 {} 중복 회원가입 실패 ", loginId);
			throw new UserAlreadyExistsException(loginId);
		}

		User requestUser = convertToUser(registerUserRequest);
		userRepository.save(requestUser);

		return RegisterUserResponse.builder()
			.name(requestUser.getName())
			.loginId(requestUser.getLoginId())
			.status(200)
			.message(SUCCESS_REGISTER).build();
	}

	@Override
	public Long deactivate(Long id, String reason) {
		//todo dto 바꾸기
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException(String.format("long id %d", id)));

		Deactivation deactivation = Deactivation.builder()
			.deactivationDate(ZonedDateTime.now())
			.reason(reason)
			.user(user).build();

		deactivationRepository.save(deactivation);

		return 0L;
	}

	@Override
	public Long activate(Long id) {
		return 0L;
	}

	@Override
	public UserInfo getUserInfoByLoginId(String loginId) {


		return null;
	}

	private User convertToUser(RegisterUserRequest request) {
		Grade grade = gradeRepository.findByName(GradeName.NORMAL)
			.orElseThrow(() -> new GradeNotFoundException(GradeName.NORMAL.name()));

		return User.builder()
			.loginId(request.loginId())
			.name(request.name())
			.grade(grade)
			.password(request.password())
			.birthday(ZonedDateTimeParser.toDate(request.birthday()))
			.createDate(ZonedDateTime.now())
			.email(request.email())
			.modifyDate(null)
			.contactNumber(request.contactNumber())
			.lastLoginDate(null)
			.status(UserStatus.ACTIVE)
			.isAdmin(false).build();
	}
}
