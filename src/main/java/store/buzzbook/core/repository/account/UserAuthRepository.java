package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
}
