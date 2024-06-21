package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import store.buzzbook.core.entity.user.User;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    boolean existsByLoginId(String loginId);
    Optional<User> findByLoginId(String loginId);
}
