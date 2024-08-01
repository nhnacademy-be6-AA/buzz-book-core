package store.buzzbook.core.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import store.buzzbook.core.common.util.UuidUtil;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserAuth;

@AllArgsConstructor
@Getter
public class OauthRegisterRequest {
	@NotEmpty(message = "이메일은 필수사항입니다.")
	@Email(message = "이메일 형식을 맞춰주십시오.")
	private String email;
	@NotNull(message = "생일은 필수사항입니다.")
	private LocalDate birthday;
	@NotEmpty(message = "연락처는 필수사항입니다.")
	@Pattern(regexp = "\\d+", message = "숫자만 입력 가능합니다.")
	private String contactNumber;
	@NotEmpty(message = "이름은 필수사항입니다.")
	@Size(max = 20)
	private String name;
	@NotNull(message = "서드파티 id는 필수입니다.")
	private String provideId;
	@NotEmpty(message = "제공자 명은 필수입니다.")
	private String provider;

	@NotEmpty
	private String loginId;
	@NotEmpty
	private String password;

	public RegisterUserRequest toRegisterUserRequest() {
		return new RegisterUserRequest(
			this.loginId,
			this.password,
			this.name,
			this.contactNumber,
			this.email,
			this.birthday
		);
	}

	public UserAuth toUserAuth(User user) {
		return UserAuth.builder()
			.user(user)
			.provider(this.provider)
			.provideId(UuidUtil.stringToByte(this.provideId))
			.build();
	}
}
