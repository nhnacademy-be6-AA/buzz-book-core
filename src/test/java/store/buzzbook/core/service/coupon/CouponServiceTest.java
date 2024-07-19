package store.buzzbook.core.service.coupon;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import store.buzzbook.core.client.auth.CouponClient;
import store.buzzbook.core.common.exception.coupon.UserCouponAlreadyExistsException;
import store.buzzbook.core.common.exception.coupon.UserCouponNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.coupon.CouponPolicyResponse;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.CouponTypeResponse;
import store.buzzbook.core.dto.coupon.CreateCouponResponse;
import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;
import store.buzzbook.core.dto.coupon.OrderCouponDetailResponse;
import store.buzzbook.core.dto.coupon.OrderCouponResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.coupon.CouponStatus;
import store.buzzbook.core.entity.user.User;
import store.buzzbook.core.entity.user.UserCoupon;
import store.buzzbook.core.repository.user.UserCouponRepository;
import store.buzzbook.core.repository.user.UserRepository;
import store.buzzbook.core.service.coupon.impl.CouponServiceImpl;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserCouponRepository userCouponRepository;

	@Mock
	private CouponClient couponClient;

	@InjectMocks
	private CouponServiceImpl couponService;

	private User testUser;
	private UserCoupon testUserCoupon;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.id(1L)
			.name("test")
			.build();

		testUserCoupon = UserCoupon.builder()
			.id(1L)
			.couponCode("aaa")
			.couponPolicyId(1)
			.user(testUser)
			.build();
	}

	@Test
	@DisplayName("create user coupon")
	void createUserCoupon() {
		// given
		DownloadCouponRequest request = new DownloadCouponRequest(testUser.getId(), 1);
		CouponTypeResponse couponTypeResponse = new CouponTypeResponse(
			1,
			"global"
		);
		CouponPolicyResponse couponPolicyResponse = new CouponPolicyResponse(
			1,
			"test",
			"amount",
			0,
			1000,
			10000,
			20000,
			14,
			LocalDate.now(),
			LocalDate.now().plusDays(1),
			false,
			couponTypeResponse
		);
		CreateCouponResponse response = new CreateCouponResponse(
			1L,
			"aaa",
			LocalDateTime.now().toString(),
			LocalDateTime.now().plusDays(1).toString(),
			CouponStatus.AVAILABLE,
			couponPolicyResponse
		);
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(userCouponRepository.existsByUserIdAndCouponPolicyId(anyLong(), anyInt())).thenReturn(false);
		when(couponClient.createCoupon(any())).thenReturn(response);
		when(userCouponRepository.save(any())).thenReturn(testUserCoupon);

		// when
		couponService.createUserCoupon(request);

		// then
		verify(userRepository, times(1)).findById(anyLong());
		verify(userCouponRepository, times(1)).existsByUserIdAndCouponPolicyId(anyLong(), anyInt());
		verify(userCouponRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("create user coupon with user not found exception")
	void createUserCouponWithUserNotFoundException() {
		// given
		DownloadCouponRequest request = new DownloadCouponRequest(testUser.getId(), 1);
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThrows(UserNotFoundException.class,
			() -> couponService.createUserCoupon(request));
	}

	@Test
	@DisplayName("create user coupon with user coupon already exists exception")
	void createUserCouponWithUserCouponAlreadyExistsException() {
		// given
		DownloadCouponRequest request = new DownloadCouponRequest(testUser.getId(), 1);
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(userCouponRepository.existsByUserIdAndCouponPolicyId(anyLong(), anyInt())).thenReturn(true);

		// when & then
		assertThrows(UserCouponAlreadyExistsException.class, () -> couponService.createUserCoupon(request));
	}

	@Test
	@DisplayName("create user coupon by batch")
	void createUserCouponByBatch() {
		// given
		CreateUserCouponRequest request = new CreateUserCouponRequest(1L, 1, "aaa");
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(userCouponRepository.existsByUserIdAndCouponPolicyId(anyLong(), anyInt())).thenReturn(false);
		when(userCouponRepository.save(any())).thenReturn(testUserCoupon);

		// when
		couponService.createUserCouponByBatch(request);

		// then
		verify(userRepository, times(1)).findById(anyLong());
		verify(userCouponRepository, times(1)).existsByUserIdAndCouponPolicyId(anyLong(), anyInt());
		verify(userCouponRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("create user coupon by batch with user not found exception")
	void createUserCouponByBatchWithUserNotFoundException() {
		// given
		CreateUserCouponRequest request = new CreateUserCouponRequest(1L, 1, "aaa");
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThrows(UserNotFoundException.class, () -> couponService.createUserCouponByBatch(request));
	}

	@Test
	@DisplayName("create user coupon by batch with user coupon already exists exception")
	void createUserCouponByBatchWithUserCouponAlreadyExistsException() {
		// given
		CreateUserCouponRequest request = new CreateUserCouponRequest(1L, 1, "aaa");
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(userCouponRepository.existsByUserIdAndCouponPolicyId(anyLong(), anyInt())).thenReturn(true);

		// when & then
		assertThrows(UserCouponAlreadyExistsException.class, () -> couponService.createUserCouponByBatch(request));
	}

	@Test
	@DisplayName("get order coupons")
	void getOrderCoupons() {
		// given
		CartDetailResponse response = CartDetailResponse.builder()
			.id(1L)
			.categoryId(1)
			.canWrap(true)
			.price(1000)
			.productName("test")
			.thumbnailPath("test")
			.quantity(1)
			.productId(1)
			.build();

		CouponTypeResponse couponTypeResponse = new CouponTypeResponse(
			1,
			"global"
		);

		CouponPolicyResponse couponPolicyResponse = new CouponPolicyResponse(
			1,
			"test",
			"amount",
			0,
			1000,
			10000,
			20000,
			14,
			LocalDate.now(),
			LocalDate.now().plusDays(1),
			false,
			couponTypeResponse
		);

		OrderCouponResponse orderCouponResponse = new OrderCouponResponse(
			"test",
			LocalDate.now(),
			LocalDate.now().plusDays(1),
			CouponStatus.AVAILABLE,
			couponPolicyResponse,
			1
		);

		when(userCouponRepository.findByUserId(anyLong())).thenReturn(List.of(testUserCoupon));
		when(couponClient.getUserCoupons(any())).thenReturn(List.of(orderCouponResponse));

		// when
		List<OrderCouponDetailResponse> responses = couponService.getOrderCoupons(1L, List.of(response));

		// then
		verify(userCouponRepository, times(1)).findByUserId(anyLong());
		verify(couponClient, times(1)).getUserCoupons(any());
		assertEquals(responses.size(), 1);
	}

	@Test
	@DisplayName("get order coupons with empty list")
	void getOrderCouponsWithEmptyList() {
		// given
		CartDetailResponse response = CartDetailResponse.builder()
			.id(1L)
			.categoryId(1)
			.canWrap(true)
			.price(1000)
			.productName("test")
			.thumbnailPath("test")
			.quantity(1)
			.productId(1)
			.build();
		when(userCouponRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());

		// when
		List<OrderCouponDetailResponse> responses = couponService.getOrderCoupons(1L, List.of(response));

		// then
		verify(userCouponRepository, times(1)).findByUserId(anyLong());
		assertEquals(responses.size(), 0);
	}

	@Test
	@DisplayName("get user coupons")
	void getUserCoupons() {
		// given
		CouponTypeResponse couponTypeResponse = new CouponTypeResponse(
			1,
			"global"
		);
		CouponPolicyResponse couponPolicyResponse = new CouponPolicyResponse(
			1,
			"test",
			"amount",
			0,
			1000,
			10000,
			20000,
			14,
			LocalDate.now(),
			LocalDate.now().plusDays(1),
			false,
			couponTypeResponse
		);
		CouponResponse response = new CouponResponse(
			1L,
			LocalDate.now(),
			LocalDate.now().plusDays(1),
			CouponStatus.AVAILABLE,
			couponPolicyResponse
		);

		when(userCouponRepository.findByUserId(anyLong())).thenReturn(List.of(testUserCoupon));
		when(couponClient.getUserCoupons(any(), anyString())).thenReturn(List.of(response));

		// when
		List<CouponResponse> responses = couponService.getUserCoupons(1L, "available");

		// then
		verify(userCouponRepository, times(1)).findByUserId(anyLong());
		verify(couponClient, times(1)).getUserCoupons(any(), anyString());
		assertEquals(responses.size(), 1);
	}

	@Test
	@DisplayName("get user coupons with user coupon not found exception")
	void getUserCouponsWithUserCouponNotFoundException() {
		// given
		when(userCouponRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());

		// when & then
		assertThrows(UserCouponNotFoundException.class, () -> couponService.getUserCoupons(1L, "available"));
	}

	@Test
	@DisplayName("get user coupons with feign exception")
	void getUserCouponsWithFeignException() {
		// given
		Long userId = 1L;
		String couponStatusName = "available";
		List<UserCoupon> userCoupons = List.of(testUserCoupon);
		when(userCouponRepository.findByUserId(anyLong())).thenReturn(userCoupons);

		Request request = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null,
			new RequestTemplate());
		FeignException feignException = new FeignException.NotFound("Not Found", request, null, null);

		when(couponClient.getUserCoupons(any(), any())).thenThrow(feignException);

		// When & Then
		List<CouponResponse> result = couponService.getUserCoupons(userId, couponStatusName);
		assertTrue(result.isEmpty());

		verify(userCouponRepository, times(1)).findByUserId(anyLong());
		verify(couponClient, times(1)).getUserCoupons(any(), any());
	}

	@Test
	@DisplayName("get user info by current birthday")
	void getUserInfoByCurrentBirthday() {
		// given
		when(userRepository.findUsersByBirthdayInCurrentMonth()).thenReturn(List.of(testUser));

		// when
		List<UserInfo> userInfos = couponService.getUserInfoByCurrentBirthday();

		// then
		verify(userRepository, times(1)).findUsersByBirthdayInCurrentMonth();
		assertEquals(userInfos.size(), 1);
	}
}
