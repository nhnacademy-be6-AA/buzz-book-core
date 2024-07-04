package store.buzzbook.core.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import store.buzzbook.core.entity.user.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

	Boolean existsByUserIdAndCouponPolicyId(long userId, int couponPolicyId);

	List<UserCoupon> findByUserId(long userId);
}
