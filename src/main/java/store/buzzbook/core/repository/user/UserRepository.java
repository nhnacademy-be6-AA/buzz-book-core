package store.buzzbook.core.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserPk;

public interface UserRepository extends JpaRepository<User, UserPk>, UserRepositoryCustom {
	boolean existsByLoginId(String loginId);

	Optional<User> findByLoginId(String loginId);

}
