package store.buzzbook.core.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	Optional<List<Address>> findAllByUserId(Long userId);

	boolean deleteByIdAndUserId(Long id, Long userId);
}

