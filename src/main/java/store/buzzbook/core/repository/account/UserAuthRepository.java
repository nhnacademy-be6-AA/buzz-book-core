package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.user.UserAuth;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {
}
