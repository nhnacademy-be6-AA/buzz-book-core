package store.buzzbook.core.repository.user;

import java.util.List;
import java.util.Optional;

import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.UserRealBill;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.User;

public interface UserRepositoryCustom {
	Optional<Grade> findGradeByLoginId(String loginId);

	Optional<Grade> findGradeByUserId(Long userId);

	List<User> findUsersByBirthdayInCurrentMonth();

	List<UserRealBill> findUserRealBillsIn3Month();

	Optional<LoginUserResponse> findLoginUserResponseByUserAuth(String provider, byte[] provideId);
}
