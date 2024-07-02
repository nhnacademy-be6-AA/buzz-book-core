package store.buzzbook.core.service.user.implement;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.coupon.UserCouponAlreadyExistsException;
import store.buzzbook.core.common.exception.user.DeactivateUserException;
import store.buzzbook.core.common.exception.user.GradeNotFoundException;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.common.service.UserProducerService;
import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.dto.coupon.CreateWelcomeCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.RegisterUserResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.Deactivation;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeLog;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserCoupon;
import store.buzzbook.core.repository.user.DeactivationRepository;
import store.buzzbook.core.repository.user.GradeLogRepository;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserCouponRepository;
import store.buzzbook.core.repository.user.UserRepository;
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
	private final UserCouponRepository userCouponRepository;

	@Transactional(readOnly = true)
	@Override
	public LoginUserResponse requestLogin(String loginId) {
		User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId));

		boolean isDeactivate = deactivationRepository.existsById(user.getId());

		if (isDeactivate) {
			log.debug("로그인 실패 : 탈퇴한 유저의 아이디 {} 입니다.", user.getId());
			throw new DeactivateUserException(loginId);
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

		Optional<Grade> gradeOptional = userRepository.findGradeByLoginId(loginId);

		if (gradeOptional.isEmpty()) {
			throw new GradeNotFoundException(userOptional.get().getId());
		}

		userOptional.get().updateLastLoginAt();

		User updatedUser = userRepository.save(userOptional.get());

		return updatedUser.toUserInfo(gradeOptional.get());
	}

	@Transactional
	@Override
	public RegisterUserResponse requestRegister(RegisterUserRequest registerUserRequest) {
		String loginId = registerUserRequest.loginId();
		String successRegister = "회원가입 성공";

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

		userProducerService.sendWelcomeCouponRequest(CreateWelcomeCouponRequest.builder()
			.userId(savedUser.getId())
			.build());

		return RegisterUserResponse.builder()
			.name(requestUser.getName())
			.loginId(requestUser.getLoginId())
			.status(200)
			.message(successRegister).build();
	}

	@Transactional
	@Override
	public boolean deactivate(Long userId, String reason) {
		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isEmpty()) {
			log.debug("탈퇴 요청 중 존재하지 않는 id의 요청 발생 : {}", userId);
			throw new UserNotFoundException(userId);
		}

		Deactivation deactivation = Deactivation.builder()
			.deactivationAt(LocalDateTime.now())
			.reason(reason)
			.user(userOptional.get()).build();

		Deactivation savedData = deactivationRepository.save(deactivation);

		userOptional.get().deactivate();

		userRepository.save(userOptional.get());

		return savedData.getUser().getId().equals(userId);
	}

	@Transactional
	@Override
	public void activate(String loginId) {
		Optional<User> userOptional = userRepository.findByLoginId(loginId);

		if (userOptional.isEmpty()) {
			log.debug("존재하지 않는 계정의 활성화 요청입니다. : {}", loginId);
			throw new UserNotFoundException(loginId);
		}

		userOptional.get().activate();

		userRepository.save(userOptional.get());
	}

	@Transactional(readOnly = true)
	@Override
	public UserInfo getUserInfoByLoginId(String loginId) {
		User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException(loginId));

		Optional<Grade> gradeOptional = userRepository.findGradeByLoginId(loginId);

		if (gradeOptional.isEmpty()) {
			throw new GradeNotFoundException(user.getId());
		}

		return user.toUserInfo(gradeOptional.get());
	}

	@Transactional
	@Override
	public void addUserCoupon(CreateUserCouponRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException(request.userId()));

		if (userCouponRepository.existsByCouponPolicyId(request.couponPolicyId())) {
			throw new UserCouponAlreadyExistsException();
		}

		UserCoupon userCoupon = UserCoupon.builder()
			.user(user)
			.couponPolicyId(request.couponPolicyId())
			.couponCode(request.couponCode())
			.build();

		userCouponRepository.save(userCoupon);
	}

	@Override
	public void downloadCoupon(DownloadCouponRequest request) {
		if (userCouponRepository.existsByCouponPolicyId(request.couponPolicyId())) {
			throw new UserCouponAlreadyExistsException();
		}

		userProducerService.sendDownloadCouponRequest(request);
	}
}
