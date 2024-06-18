package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.user.Deactivation;

public interface DeactivationRepository extends JpaRepository<Deactivation, Long> {
}
