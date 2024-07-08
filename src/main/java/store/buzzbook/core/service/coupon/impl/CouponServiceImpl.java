package store.buzzbook.core.service.coupon.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.client.auth.CouponClient;
import store.buzzbook.core.common.exception.coupon.UserCouponAlreadyExistsException;
import store.buzzbook.core.common.exception.coupon.UserCouponNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.coupon.CouponLogRequest;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.CreateCouponRequest;
import store.buzzbook.core.dto.coupon.CreateCouponResponse;
import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.dto.coupon.DeleteUserCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;
import store.buzzbook.core.dto.coupon.OrderCouponDetailResponse;
import store.buzzbook.core.dto.coupon.OrderCouponResponse;
import store.buzzbook.core.dto.coupon.UpdateCouponRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.coupon.CouponStatus;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserCoupon;
import store.buzzbook.core.repository.user.UserCouponRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.coupon.CouponService;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponClient couponClient;

	@Transactional
	@Override
	public void createUserCoupon(DownloadCouponRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException(request.userId()));

		if (Boolean.TRUE.equals(
			userCouponRepository.existsByUserIdAndCouponPolicyId(request.userId(), request.couponPolicyId()))) {
			throw new UserCouponAlreadyExistsException();
		}

		CreateCouponResponse response = couponClient.createCoupon(CreateCouponRequest.builder()
			.couponPolicyId(request.couponPolicyId())
			.build());

		UserCoupon userCoupon = UserCoupon.builder()
			.user(user)
			.couponPolicyId(response.couponPolicyResponse().id())
			.couponCode(response.couponCode())
			.build();

		userCouponRepository.save(userCoupon);
	}

	@Transactional
	@Override
	public void createUserCouponByBatch(CreateUserCouponRequest request) {
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> new UserNotFoundException(request.userId()));

		if (Boolean.TRUE.equals(
			userCouponRepository.existsByUserIdAndCouponPolicyId(request.userId(), request.couponPolicyId()))) {
			throw new UserCouponAlreadyExistsException();
		}

		UserCoupon userCoupon = UserCoupon.builder()
			.user(user)
			.couponPolicyId(request.couponPolicyId())
			.couponCode(request.couponCode())
			.build();

		userCouponRepository.save(userCoupon);
	}

	@Transactional(readOnly = true)
	@Override
	public List<OrderCouponDetailResponse> getOrderCoupons(Long userId, List<CartDetailResponse> responses) {
		List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

		if (userCoupons.isEmpty()) {
			throw new UserCouponNotFoundException();
		}

		List<CouponLogRequest> request = userCoupons.stream()
			.map(CouponLogRequest::from)
			.toList();

		List<Integer> targetIds = responses.stream()
			.flatMap(cartDetail -> List.of(cartDetail.getProductId(), cartDetail.getCategoryId()).stream())
			.toList();

		List<OrderCouponResponse> coupons = couponClient.getUserCoupons(request);

		return coupons.stream()
			.filter(coupon -> targetIds.contains(coupon.targetId()))
			.map(coupon -> OrderCouponDetailResponse.builder()
				.couponCode(coupon.code())
				.couponPolicyId(coupon.couponPolicyResponse().id())
				.couponPolicyName(coupon.couponPolicyResponse().name())
				.couponPolicyDiscountType(coupon.couponPolicyResponse().discountType())
				.couponPolicyDiscountRate(coupon.couponPolicyResponse().discountRate())
				.couponPolicyDiscountAmount(coupon.couponPolicyResponse().discountAmount())
				.couponPolicyStandardPrice(coupon.couponPolicyResponse().standardPrice())
				.couponPolicyMaxDiscountAmount(coupon.couponPolicyResponse().maxDiscountAmount())
				.couponPolicyIdTargetId(coupon.targetId())
				.build())
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public List<CouponResponse> getUserCoupons(Long userId, String couponStatusName) {
		List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);

		if (userCoupons.isEmpty()) {
			throw new UserCouponNotFoundException();
		}

		List<CouponLogRequest> request = userCoupons.stream()
			.map(CouponLogRequest::from)
			.toList();

		try {
			return couponClient.getUserCoupons(request, couponStatusName);
		} catch (FeignException e) {
			if (e.status() == 404) {
				return Collections.emptyList();
			} else {
				throw e;
			}
		}
	}

	@Transactional
	@Override
	public void deleteUserCoupon(Long userId, DeleteUserCouponRequest request) {
		if (Boolean.FALSE.equals(
			userCouponRepository.existsByUserIdAndCouponPolicyId(userId, request.couponPolicyId()))) {
			throw new UserCouponNotFoundException();
		}

		couponClient.updateCoupon(UpdateCouponRequest.builder()
			.couponCode(request.couponCode())
			.status(CouponStatus.USED)
			.build());
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserInfo> getUserInfoByCurrentBirthday() {
		return userRepository.findUsersByBirthdayInCurrentMonth().stream()
			.map(user -> UserInfo.builder()
				.id(user.getId())
				.loginId(user.getLoginId())
				.contactNumber(user.getContactNumber())
				.name(user.getName())
				.email(user.getEmail())
				.birthday(user.getBirthday())
				.build())
			.toList();
	}
}
