package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.Deactivation;

public interface DeactivationRepository extends JpaRepository<Deactivation, Long> {
}
