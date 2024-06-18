package store.buzzbook.core.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import store.buzzbook.account.domain.address.Address;

import java.util.List;
import java.util.Optional;

public interface AddressRepository  extends JpaRepository<Address, Long> {
	Optional<List<Address>> findAllByUserId(Long userId);
}

