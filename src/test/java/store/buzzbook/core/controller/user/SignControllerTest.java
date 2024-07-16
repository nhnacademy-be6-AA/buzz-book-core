package store.buzzbook.core.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.user.LoginUserResponse;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.user.Grade;
import store.buzzbook.core.entity.user.GradeName;
import store.buzzbook.core.service.user.UserService;

@WebMvcTest(SignController.class)
class SignControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper mapper;

	private String password;
	private UserInfo userInfo;
	private RegisterUserRequest registerUserRequest;
	private LoginUserResponse loginUserResponse;
	private Grade grade;

	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {
		password = "asdi2u34911!oj$@eI723";
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

		registerUserRequest = RegisterUserRequest.builder()
			.name(userInfo.name())
			.birthday(userInfo.birthday())
			.password(password)
			.contactNumber(userInfo.contactNumber())
			.email(userInfo.email())
			.loginId(userInfo.loginId())
			.build();

		loginUserResponse = LoginUserResponse.builder()
			.loginId(userInfo.loginId())
			.isAdmin(userInfo.isAdmin())
			.password(password)
			.build();
	}

	@Test
	@DisplayName("회원가입 성공")
	void testRegister() throws Exception {
		Mockito.doNothing().when(userService).requestRegister(registerUserRequest);

		String requestBody = mapper.writeValueAsString(registerUserRequest);

		mockMvc.perform(post("/api/account/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk());
		Mockito.verify(userService, Mockito.times(1)).requestRegister(registerUserRequest);
	}

	@Test
	@DisplayName("로그인 시도 성공")
	void testLogin() throws Exception {
		Mockito.when(userService.requestLogin(userInfo.loginId())).thenReturn(loginUserResponse);

		String expectJson = mapper.writeValueAsString(loginUserResponse);

		mockMvc.perform(post("/api/account/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerUserRequest.loginId()))
			.andExpect(status().isOk())
			.andExpect(content().json(expectJson));
		Mockito.verify(userService, Mockito.times(1)).requestLogin(userInfo.loginId());
	}

	@Test
	@DisplayName("로그인 시도 성공")
	void testLoginSuccess() throws Exception {
		Mockito.when(userService.successLogin(userInfo.loginId())).thenReturn(userInfo);

		String expectJson = mapper.writeValueAsString(userInfo);

		mockMvc.perform(put("/api/account/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userInfo.loginId()))
			.andExpect(status().isOk())
			.andExpect(content().json(expectJson));

		Mockito.verify(userService, Mockito.times(1)).successLogin(userInfo.loginId());
	}

	@Test
	@DisplayName("유저 활성화 성공")
	void testActivate() throws Exception {
		Mockito.doNothing().when(userService).activate(userInfo.loginId());

		mockMvc.perform(put("/api/account/activate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userInfo.loginId()))
			.andExpect(status().isOk());

		Mockito.verify(userService, Mockito.times(1)).activate(userInfo.loginId());
	}
}
