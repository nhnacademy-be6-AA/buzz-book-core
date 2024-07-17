package store.buzzbook.core.controller.coupon;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.cart.CartDetailResponse;
import store.buzzbook.core.dto.coupon.CreateUserCouponRequest;
import store.buzzbook.core.dto.coupon.DownloadCouponRequest;
import store.buzzbook.core.dto.coupon.OrderCouponDetailResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.service.coupon.CouponService;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CouponService couponService;

	@Test
	@DisplayName("download coupon")
	void downloadCoupon() throws Exception {
		// given
		DownloadCouponRequest request = new DownloadCouponRequest(1L, 1);
		doNothing().when(couponService).createUserCoupon(request);

		// when & then
		mockMvc.perform(post("/api/account/coupons")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("create user coupon by batch")
	void createUserCouponBatch() throws Exception {
		// given
		CreateUserCouponRequest request = new CreateUserCouponRequest(1L, 1, "aaa");
		doNothing().when(couponService).createUserCouponByBatch(request);

		// when & then
		mockMvc.perform(post("/api/account/coupons/batch")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("get user by birthday")
	void getUserCouponByBirthday() throws Exception {
		// given
		UserInfo testUserInfo = UserInfo.builder()
			.id(1L)
			.loginId("test")
			.name("test")
			.build();
		when(couponService.getUserInfoByCurrentBirthday()).thenReturn(List.of(testUserInfo));

		// when & then
		mockMvc.perform(get("/api/account/coupons/birthday"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(testUserInfo.id()))
			.andExpect(jsonPath("$[0].loginId").value(testUserInfo.loginId()))
			.andExpect(jsonPath("$[0].name").value(testUserInfo.name()));
	}

	@Test
	@DisplayName("get order coupons")
	void getOrderCoupons() throws Exception {
		// given
		CartDetailResponse request = CartDetailResponse.builder()
			.id(1L)
			.productId(1)
			.productName("test")
			.build();

		OrderCouponDetailResponse response = OrderCouponDetailResponse.builder()
			.couponCode("test")
			.couponPolicyId(1)
			.build();
		when(couponService.getOrderCoupons(anyLong(), any())).thenReturn(List.of(response));

		// when & then
		mockMvc.perform(post("/api/account/coupons/order")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(List.of(request))))
			.andExpect(status().isOk());
	}
}
