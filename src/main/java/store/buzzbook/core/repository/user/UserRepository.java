package store.buzzbook.core.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
	boolean existsByLoginId(String loginId);

	Optional<User> findByLoginId(String loginId);

}
