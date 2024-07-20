package store.buzzbook.core.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.order.DeliveryPolicy;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Integer> {
	boolean existsByName(String name);
}
