package store.buzzbook.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

	boolean existsByCouponPolicyId(int couponPolicyId);
}
