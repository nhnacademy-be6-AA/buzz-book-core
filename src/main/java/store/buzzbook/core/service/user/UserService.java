package store.buzzbook.core.service.user;

import store.buzzbook.core.dto.user.ChangePasswordRequest;
import store.buzzbook.core.dto.user.DeactivateUserRequest;
import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.dto.user.UserInfo;

/**
 * 회원 관리를 위한 서비스입니다.
 * @author Heldenarr, park
 */
public interface UserService {
	/**
	 * 로그인 요청 서비스입니다.
	 *
	 * @author Heldenarr
	 * @param loginId 회원의 로그인를 위한 아이디입니다.
	 * @throws DeactivatedUserException 만약 로그인 요청한 회원의 loginId가 탈퇴한 회원일 경우.
	 * @return 회원의 인코딩된 비밀번호와 id, 관리자 여부를 포함한 회원정보 LoginUserResponse를 전달합니다.
	 */
	LoginUserResponse requestLogin(String loginId);

	/**
	 * 주어진 로그인 ID로 성공적인 로그인 후 로그인 정보를 업데이트 후 사용자 정보를 반환합니다.
	 *
	 * @author Heldenarr
	 * @param loginId 사용자의 로그인 ID
	 * @return 사용자 정보를 포함하는 UserInfo 객체, 등급, 포인트 정보도 포함합니다.
	 * @throws UserNotFoundException 존재하지 않는 회원의 로그인 성공 요청
	 * @throws GradeNotFoundException 회원은 존재하나 등급 조회에 실패
	 */
	UserInfo successLogin(String loginId);

	/**
	 * 유저의 기본 정보로 회원가입을 수행합니다.
	 *
	 * @author Heldenarr, park
	 * @param registerUserRequest 회원가입에 필요한 사용자의 기본정보(생일, 로그인 아이디, 비밀번호, 이름, 연락처, 이메일)를 포함한다.
	 * @throws GradeNotFoundException 기본 등급을 가져오는데에 실패
	 * @throws UserAlreadyExistsException 이미 존재하는 로그인 ID로 회원가입을 요청했습니다.
	 *
	 */
	void requestRegister(RegisterUserRequest registerUserRequest);

	/**
	 * 주어진 사용자 ID로 사용자 계정을 탈퇴 처리합니다.
	 *
	 * @author Heldenarr
	 * @param userId 탈퇴할 회원의 ID
	 * @param deactivateUserRequest 탈퇴할 회원을 인증할 비밀번호와 탈퇴 사유를 담은 객체
	 * @throws UserNotFoundException 존재하지 않는 회원의 탈퇴요청입니다.
	 * @throws PasswordIncorrectException 비밀번호 인증에 실패했습니다.
	 */
	void deactivate(Long userId, DeactivateUserRequest deactivateUserRequest);

	/**
	 * 주어진 로그인 ID로 휴면 상태의 사용자 계정을 활성화합니다.
	 *
	 * @author Heldenarr
	 * @param loginId 활성화할 사용자의 로그인 ID
	 * @throws UserNotFoundException 존재하지 않는 회원의 활성화 요청입니다.
	 */
	void activate(String loginId);

	/**
	 * 주어진 사용자 ID로 사용자 정보를 업데이트합니다.
	 *
	 * @author Heldenarr
	 * @param userId 업데이트할 사용자의 ID
	 * @param updateUserRequest 업데이트할 사용자 정보를 포함하는 요청 객체. 연락처, 이름, 이메일의 수정만 받는다.
	 * @throws UserNotFoundException 존재하지 않는 회원의 정보 수정 요청입니다.
	 */
	void updateUserInfo(Long userId, UpdateUserRequest updateUserRequest);

	/**
	 * 주어진 사용자 ID로 사용자 정보를 조회합니다.
	 *
	 * @author Heldenarr
	 * @param userId 조회할 사용자의 ID
	 * @return 사용자 정보를 포함하는 UserInfo 객체. id, 로그인 id, 연락처, 이름, 이메일, 생일, 등급, 관리자여부, 포인트 등을 포함한다.
	 * @throws UserNotFoundException 존재하지 않는 회원의 정보 조회 요청입니다.
	 * @throws GradeNotFoundException 회원은 발견했으나 등급 조회에 실패했습니다.
	 *
	 */
	UserInfo getUserInfoByUserId(Long userId);

	/**
	 * 주어진 로그인 ID로 사용자 정보를 조회합니다.
	 *
	 * @author Heldenarr
	 * @param loginId 조회할 사용자의 로그인 ID
	 * @return 사용자 정보를 포함하는 UserInfo 객체. id, 로그인 id, 연락처, 이름, 이메일, 생일, 등급, 관리자여부, 포인트 등을 포함한다.
	 * @throws UserNotFoundException 존재하지 않는 회원의 정보 조회 요청입니다.
	 * @throws GradeNotFoundException 회원은 발견했으나 등급 조회에 실패했습니다.
	 *
	 */
	UserInfo getUserInfoByLoginId(String loginId);

	/**
	 * 주어진 user ID로 사용자의 비밀번호를 변경합니다.
	 *
	 * @author Heldenarr
	 * @param userId 조회할 사용자 ID
	 * @param changePasswordRequest 인증에 사용될 예전 비밀번호, 인코딩된 새로운 비밀번호, 인코딩 되지 않은 새로운 비밀번호 확인이 포함된 객체입니다.
	 * @throws UnEncryptedPasswordException 인코딩 되지 않은 인코딩된 새로운 비밀번호가 있습니다.
	 * @throws UserNotFoundException 존재 하지 않는 회원의 비밀번호 변경 요청입니다.
	 * @throws PasswordIncorrectException 새로운 비밀번호와 비밀번호 확인이 다르거나, 이전 비밀번호 인증에 실패했습니다.
	 *
	 */
	void updatePassword(Long userId, ChangePasswordRequest changePasswordRequest);
}
