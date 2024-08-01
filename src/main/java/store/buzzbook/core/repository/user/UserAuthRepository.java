package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
	boolean existsByProvideIdAndProvider(byte[] provideId, String provider);
}
