package store.buzzbook.core.service.user.implement;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.repository.account.UserRepository;
import store.buzzbook.core.service.user.UserService;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;


	@Override
	public LoginUserResponse requestLogin(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
		return LoginUserResponse.convertFrom(user);
	}

	@Override
	public UserInfo successLogin(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
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
	public Long requestSignUp(UserInfo userInfo) {
		return 0L;
	}

	@Override
	public Long deactivate(Long id) {
		return 0L;
	}

	@Override
	public Long activate(Long id) {
		return 0L;
	}
}
