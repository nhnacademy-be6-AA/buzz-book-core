package store.buzzbook.core.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.coupon.CouponPolicyResponse;
import store.buzzbook.core.dto.coupon.CouponResponse;
import store.buzzbook.core.dto.coupon.CouponTypeResponse;
import store.buzzbook.core.dto.point.PointLogResponse;
import store.buzzbook.core.dto.user.ChangePasswordRequest;
import store.buzzbook.core.dto.user.DeactivateUserRequest;
import store.buzzbook.core.dto.user.UpdateUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.coupon.CouponStatus;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.coupon.CouponService;
import store.buzzbook.core.service.point.PointService;
import store.buzzbook.core.service.user.UserService;

@WebMvcTest(MyPageController.class)
class MyPageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private PointService pointService;

	@MockBean
	private CouponService couponService;

	@Autowired
	private ObjectMapper objectMapper;

	private Long userId;
	private ChangePasswordRequest changePasswordRequest;
	private UpdateUserRequest updateUserRequest;
	private DeactivateUserRequest deactivateUserRequest;
	private UserInfo userInfo;
	private List<CouponResponse> couponResponseList;
	private Page<PointLogResponse> pointLogResponseList;
	private Grade grade;

	@BeforeEach
	void setUp() {
		userId = 1L;

		changePasswordRequest = new ChangePasswordRequest(
			"asd123",
			"asd1234",
			"asd1234"

		);

		updateUserRequest = new UpdateUserRequest(
			"이름",
			"01077778888",
			"asd123@address.org"
		);

		deactivateUserRequest = new DeactivateUserRequest(
			"asd123",
			"탈퇴이유"
		);

		grade = Grade.builder()
			.benefit(2.5)
			.name(GradeName.NORMAL)
			.standard(200000)
			.build();

		userInfo = UserInfo.builder()
			.loginId("testid00000000")
			.name("john doe")
			.email("email123@nhn.com")
			.contactNumber("010-0000-1111")
			.birthday(LocalDate.now())
			.id(1L)
			.point(132)
			.grade(grade.toResponse())
			.isAdmin(false).build();

		CouponTypeResponse couponTypeResponse = new CouponTypeResponse(1, "쿠폰 타입");

		CouponPolicyResponse couponPolicyResponse = new CouponPolicyResponse(
			1,
			"여름 할인",
			"비율",
			0.15,
			0,
			1000,
			5000,
			30,
			LocalDate.of(2024, 7, 1),
			LocalDate.of(2024, 7, 31),
			false,
			couponTypeResponse
		);

		couponResponseList = List.of(new CouponResponse(
			1L,
			LocalDate.now(),
			LocalDate.MAX,
			CouponStatus.AVAILABLE,
			couponPolicyResponse
		), new CouponResponse(
			2L,
			LocalDate.now().minusDays(1),
			LocalDate.MAX.minusDays(1),
			CouponStatus.AVAILABLE,
			couponPolicyResponse
		));

		pointLogResponseList = new PageImpl<>(List.of(new PointLogResponse(
			LocalDateTime.now().minusDays(1),
			"어쩌고",
			100,
			1100
		), new PointLogResponse(
			LocalDateTime.now(),
			"저쩌고",
			1100,
			2200
		)));
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void testChangePassword() throws Exception {
		mockMvc.perform(put("/api/account/mypage/password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(changePasswordRequest))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk());

		Mockito.verify(userService, Mockito.times(1))
			.updatePassword(Mockito.eq(userId), Mockito.any(ChangePasswordRequest.class));
	}

	@Test
	@DisplayName("유저정보 변경 성공")
	void testUpdateUser() throws Exception {
		mockMvc.perform(put("/api/account/mypage")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateUserRequest))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk());

		Mockito.verify(userService, Mockito.times(1))
			.updateUserInfo(Mockito.eq(userId), Mockito.any(UpdateUserRequest.class));
	}

	@Test
	@DisplayName("유저 탈퇴 성공")
	void testDeactivateUser() throws Exception {
		mockMvc.perform(put("/api/account/mypage/deactivate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(deactivateUserRequest))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk());

		Mockito.verify(userService, Mockito.times(1))
			.deactivate(Mockito.eq(userId), Mockito.any(DeactivateUserRequest.class));
	}

	@Test
	@DisplayName("유저정보 얻기 성공")
	void testGetUserInfo() throws Exception {
		Mockito.when(userService.getUserInfoByUserId(userId)).thenReturn(userInfo);

		String expectJson = objectMapper.writeValueAsString(userInfo);

		mockMvc.perform(get("/api/account/mypage")
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(content().json(expectJson));

		Mockito.verify(userService, Mockito.times(1)).getUserInfoByUserId(userId);
	}

	@Test
	@DisplayName("유저 쿠폰 얻기 성공")
	void testGetUserCoupons() throws Exception {
		String statusName = "ACTIVE";
		Mockito.when(couponService.getUserCoupons(userId, statusName))
			.thenReturn(couponResponseList);

		String expectJson = objectMapper.writeValueAsString(couponResponseList);

		mockMvc.perform(post("/api/account/mypage/coupons")
				.contentType(MediaType.APPLICATION_JSON)
				.content(statusName)
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(content().json(expectJson));

		Mockito.verify(couponService, Mockito.times(1)).getUserCoupons(userId, statusName);
	}

	@Test
	@DisplayName("유저 포인트 로그 얻기 성공")
	void testGetPointLogs() throws Exception {
		Pageable pageable = PageRequest.of(1, 10);
		Mockito.when(pointService.getPointLogs(pageable, userId)).thenReturn(pointLogResponseList);

		mockMvc.perform(get("/api/account/mypage/points")
				.param("page", "1")
				.param("size", "10")
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(pointLogResponseList.getContent().size()));

		Mockito.verify(pointService, Mockito.times(1)).getPointLogs(pageable, userId);
	}
}