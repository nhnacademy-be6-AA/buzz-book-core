package store.buzzbook.core.service.coupon;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.dto.coupon.DeleteUserCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;
import store.buzzbook.core.dto.coupon.OrderCouponDetailResponse;
import store.buzzbook.core.dto.user.UserInfo;

public interface CouponService {

	void createUserCoupon(DownloadCouponRequest request);

	void createUserCouponByBatch(CreateUserCouponRequest request);

	List<CouponResponse> getUserCoupons(Long userId, String couponStatusName);

	List<OrderCouponDetailResponse> getOrderCoupons(Long userId, List<CartDetailResponse> responses);

	void deleteUserCoupon(Long userId, String couponCode, HttpServletRequest request);

	List<UserInfo> getUserInfoByCurrentBirthday();
}
