package store.buzzbook.core.service.user.implement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.common.util.UuidUtil;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.OauthRegisterRequest;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.user.GradeLogRepository;
import store.buzzbook.core.repository.user.GradeRepository;
import store.buzzbook.core.repository.user.UserAuthRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.user.UserAuthService;
import store.buzzbook.core.service.user.UserService;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
	private final UserService userService;
	private final UserAuthRepository authRepository;
	private final UserRepository userRepository;
	private final GradeRepository gradeRepository;
	private final GradeLogRepository gradeLogRepository;

	@Override
	public boolean isRegistered(String provideId, String provider) {
		byte[] idBytes = UuidUtil.stringToByte(provideId);
		return authRepository.existsByProvideIdAndProvider(idBytes, provider);
	}

	@Transactional
	@Override
	public void register(OauthRegisterRequest oauthRegisterRequest) {
		if (isRegistered(oauthRegisterRequest.getProvideId(), oauthRegisterRequest.getProvider())
			|| userRepository.existsByLoginId(oauthRegisterRequest.getLoginId())) {
			throw new UserAlreadyExistsException(oauthRegisterRequest.getProvideId());
		}

		userService.requestRegister(oauthRegisterRequest.toRegisterUserRequest());

		User savedUser = userRepository.findByLoginId(oauthRegisterRequest.getLoginId())
			.orElseThrow(UserNotFoundException::new);

		authRepository.save(oauthRegisterRequest.toUserAuth(savedUser));
	}

	@Override
	public LoginUserResponse getLoginUser(String provideId, String provider) {
		byte[] idBytes = UuidUtil.stringToByte(provideId);

		return userRepository.findLoginUserResponseByUserAuth(provider, idBytes)
			.orElseThrow(UserNotFoundException::new);
	}

	private boolean isPasswordEncrypted(String password) {
		return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
	}
}
