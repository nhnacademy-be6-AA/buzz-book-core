package store.buzzbook.core.service.user.implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.buzzbook.core.common.exception.user.GradeNotFoundException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.UserService;

import java.time.ZonedDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final GradeRepository gradeRepository;

	private final String SUCCESS_REGISTER = "회원가입 성공";


	@Override
	public LoginUserResponse requestLogin(String loginId) {
		User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId));
		return LoginUserResponse.convertFrom(user);
	}

	@Override
	public UserInfo successLogin(Long id) {
//		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
//		return UserInfo.builder()
//			.loginId(user.getLoginId())
//			.id(user.getId())
//			.name(user.getName())
//			.email(user.getEmail())
//			.grade(user.getGrade())
//			.birthday(user.getBirthday())
//			.contactNumber(user.getContactNumber())
//			.isAdmin(user.isAdmin()).build();
		return null;
	}

	@Override
	public RegisterUserResponse requestRegister(RegisterUserRequest registerUserRequest) {
		String loginId = registerUserRequest.loginId();

		if(userRepository.existsByLoginId(loginId)){
			log.warn("유저 아이디 {} 중복 회원가입 실패 ", loginId);
			throw new UserAlreadyExistsException(loginId);
		}

		User requestUser = convertToUser(registerUserRequest);
		User registeredUser = userRepository.save(requestUser);

		return RegisterUserResponse.builder()
				.name(registeredUser.getName())
				.loginId(registeredUser.getLoginId())
				.status(200)
				.message(SUCCESS_REGISTER).build();
	}

	@Override
	public Long deactivate(Long id) {
		return 0L;
	}

	@Override
	public Long activate(Long id) {
		return 0L;
	}


	private User convertToUser(RegisterUserRequest request) {
		Grade grade = gradeRepository.findByName(GradeName.NORMAL)
				.orElseThrow(() -> new GradeNotFoundException(GradeName.NORMAL.name()));

		return User.builder()
				.loginId(request.loginId())
				.name(request.name())
				.grade(grade)
				.birthday(request.birthday())
				.createDate(ZonedDateTime.now())
				.email(request.email())
				.modifyDate(null)
				.contactNumber(request.contactNumber())
				.lastLoginDate(null)
				.isAdmin(false).build();
	}
}
