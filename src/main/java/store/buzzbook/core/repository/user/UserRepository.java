package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long>  {
}
