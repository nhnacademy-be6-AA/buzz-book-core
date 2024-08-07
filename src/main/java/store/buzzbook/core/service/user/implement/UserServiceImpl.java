package store.buzzbook.core.service.user.implement;

import static store.buzzbook.core.common.listener.PointPolicyListener.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.client.auth.CouponClient;
import store.buzzbook.core.common.exception.user.DeactivatedUserException;
import store.buzzbook.core.common.exception.user.DormantUserException;
import store.buzzbook.core.common.exception.user.GradeNotFoundException;
import store.buzzbook.core.common.exception.user.PasswordIncorrectException;
import store.buzzbook.core.common.exception.user.UnEncryptedPasswordException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.common.service.UserProducerService;
import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;
import store.buzzbook.core.dto.user.ChangePasswordRequest;
import store.buzzbook.core.dto.user.DeactivateUserRequest;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.dto.user.UserRealBill;
import store.buzzbook.core.entity.point.PointLog;
import store.buzzbook.core.entity.point.PointPolicy;
import store.buzzbook.core.entity.user.Deactivation;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeLog;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserStatus;
import store.buzzbook.core.repository.point.PointLogRepository;
import store.buzzbook.core.repository.point.PointPolicyRepository;
import store.buzzbook.core.repository.user.DeactivationRepository;
import store.buzzbook.core.repository.user.GradeLogRepository;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserCouponRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.product.ProductService;
import store.buzzbook.core.service.user.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final GradeRepository gradeRepository;
	private final DeactivationRepository deactivationRepository;
	private final GradeLogRepository gradeLogRepository;
	private final UserProducerService userProducerService;
	private final PointLogRepository pointLogRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponClient couponClient;
	private final ProductService productService;
	private final PointPolicyRepository pointPolicyRepository;

	@Transactional(readOnly = true)
	@Override
	public LoginUserResponse requestLogin(String loginId) {
		User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId));

		if (user.getStatus().equals(UserStatus.WITHDRAW)) {
			log.debug("로그인 실패 : 탈퇴한 유저의 아이디 {} 입니다.", user.getId());
			throw new DeactivatedUserException(loginId);
		}

		return LoginUserResponse.convertFrom(user);
	}

	@Transactional
	@Override
	public UserInfo successLogin(String loginId) {
		log.debug("최근 로그인 일자 업데이트 : {} ", loginId);
		Optional<User> userOptional = userRepository.findByLoginId(loginId);

		if (userOptional.isEmpty()) {
			throw new UserNotFoundException(loginId);
		}

		if (userOptional.get().getStatus().equals(UserStatus.DORMANT)) {
			log.debug("로그인 실패. 휴면 계정입니다.");
			throw new DormantUserException();
		}

		Optional<Grade> gradeOptional = userRepository.findGradeByLoginId(loginId);

		if (gradeOptional.isEmpty()) {
			throw new GradeNotFoundException(userOptional.get().getId());
		}

		userOptional.get().updateLastLoginAt();
		User updatedUser = userRepository.save(userOptional.get());

		PointLog pointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(updatedUser.getId());
		int point = 0;
		if (Objects.nonNull(pointLog)) {
			point = pointLog.getBalance();
		}

		return updatedUser.toUserInfo(gradeOptional.get(), point);
	}

	@Transactional
	@Override
	public void requestRegister(RegisterUserRequest registerUserRequest) {
		log.debug("회원가입 시도 : {}", registerUserRequest.loginId());
		String loginId = registerUserRequest.loginId();

		Grade grade = gradeRepository.findByName(GradeName.NORMAL)
			.orElseThrow(() -> new GradeNotFoundException(GradeName.NORMAL.name()));

		if (userRepository.existsByLoginId(loginId)) {
			log.debug("유저 아이디 {} 중복 회원가입 실패 ", loginId);
			throw new UserAlreadyExistsException(loginId);
		}

		User requestUser = registerUserRequest.toUser();
		User savedUser = userRepository.save(requestUser);

		GradeLog gradeLog = GradeLog.builder()
			.grade(grade)
			.user(savedUser)
			.changeAt(LocalDateTime.now())
			.build();

		gradeLogRepository.save(gradeLog);

		PointPolicy pointPolicy = pointPolicyRepository.findByName(SIGN_UP);
		PointLog pointLog = PointLog.builder()
			.user(savedUser)
			.createdAt(LocalDateTime.now())
			.delta(pointPolicy.getPoint())
			.inquiry(pointPolicy.getName())
			.balance(pointPolicy.getPoint())
			.build();

		pointLogRepository.save(pointLog);

		userProducerService.sendWelcomeCouponRequest(CreateWelcomeCouponRequest.builder()
			.userId(savedUser.getId())
			.build());
	}

	@Transactional
	@Override
	public void deactivate(Long userId, DeactivateUserRequest deactivateUserRequest) {
		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isEmpty()) {
			log.debug("탈퇴 요청 중 존재하지 않는 id의 요청 발생 : {}", userId);
			throw new UserNotFoundException(userId);
		}

		if (!BCrypt.checkpw(deactivateUserRequest.password(), userOptional.get().getPassword())) {
			log.debug("탈퇴 요청 중 비밀번호가 틀렸습니다. : {}", userId);
			throw new PasswordIncorrectException();
		}

		Deactivation deactivation = Deactivation.builder()
			.deactivationAt(LocalDateTime.now())
			.reason(deactivateUserRequest.reason())
			.user(userOptional.get()).build();

		deactivationRepository.save(deactivation);

		userOptional.get().deactivate();

		userRepository.save(userOptional.get());
	}

	@Transactional
	@Override
	public void activate(String loginId) {
		Optional<User> userOptional = userRepository.findByLoginId(loginId);

		if (userOptional.isEmpty()) {
			log.debug("존재하지 않는 계정의 활성화 요청입니다. : {}", loginId);
			throw new UserNotFoundException(loginId);
		} else if (userOptional.get().getStatus().equals(UserStatus.WITHDRAW)) {
			log.debug("탈퇴한 계정의 활성화 요청입니다. : {}", loginId);
			throw new DeactivatedUserException(loginId);
		} else if (userOptional.get().getStatus().equals(UserStatus.ACTIVE)) {
			log.debug("이미 활성화된 계정의 활성화 요청입니다. : {}", loginId);
			throw new UserAlreadyExistsException(loginId);
		}

		userOptional.get().activate();
		userRepository.save(userOptional.get());
	}

	@Transactional
	@Override
	public void updateUserInfo(Long userId, UpdateUserRequest updateUserRequest) {
		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			log.debug("존재하지 않는 userId 입니다. : {}", userId);
			throw new UserNotFoundException(userId);
		}

		user.get().updateUserBy(updateUserRequest);
		userRepository.save(user.get());
	}

	@Transactional(readOnly = true)
	@Override
	public UserInfo getUserInfoByUserId(Long userId) {
		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			throw new UserNotFoundException(userId);
		}

		Optional<Grade> grade = userRepository.findGradeByLoginId(user.get().getLoginId());

		if (grade.isEmpty()) {
			throw new GradeNotFoundException(userId);
		}

		PointLog pointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(user.get().getId());
		int point = 0;

		if (Objects.nonNull(pointLog)) {
			point = pointLog.getBalance();
		}

		return user.get().toUserInfo(grade.get(), point);
	}

	@Transactional(readOnly = true)
	@Override
	public UserInfo getUserInfoByLoginId(String loginId) {
		User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId));
		Optional<Grade> gradeOptional = userRepository.findGradeByLoginId(loginId);

		if (gradeOptional.isEmpty()) {
			throw new GradeNotFoundException(user.getId());
		}

		PointLog pointLog = pointLogRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());

		int point = 0;

		if (Objects.nonNull(pointLog)) {
			point = pointLog.getBalance();
		}

		return user.toUserInfo(gradeOptional.get(), point);
	}

	@Override
	public void updatePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
		Optional<User> user = userRepository.findById(userId);

		if (user.isEmpty()) {
			log.debug("잘못된 유저id의 요청으로 비밀번호 변경에 실패했습니다. : {}", userId);
			throw new UserNotFoundException(userId);
		}

		String password = user.get().getPassword();
		if (!BCrypt.checkpw(changePasswordRequest.oldPassword(), password)) {
			log.debug("비밀번호가 틀렸습니다.");
			throw new PasswordIncorrectException();
		}

		if (!isPasswordEncrypted(changePasswordRequest.newPassword())) {
			log.debug("암호화되지 않은 비밀번호입니다. 프론트 서버를 확인해주세요.");
			throw new UnEncryptedPasswordException(userId);
		} else if (!BCrypt.checkpw(changePasswordRequest.confirmPassword(), changePasswordRequest.newPassword())) {
			log.debug("새 비밀번호 확인이 다릅니다.");
			throw new PasswordIncorrectException();
		}

		user.get().changePassword(changePasswordRequest.newPassword());
		userRepository.save(user.get());
	}

	@Override
	public List<UserRealBill> getUserRealBills() {
		return userRepository.findUserRealBillsIn3Month();
	}

	private boolean isPasswordEncrypted(String password) {
		return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
	}
}
