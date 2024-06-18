package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
