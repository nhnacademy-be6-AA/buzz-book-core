package store.buzzbook.core.controller.user;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.common.config.JacksonConfig;
import store.buzzbook.core.dto.user.RegisterUserRequest;
import store.buzzbook.core.service.user.UserService;

@WebMvcTest(SignController.class)
@Import(JacksonConfig.class)
@AutoConfigureMockMvc
class SignControllerTest {
	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper mapper = new ObjectMapper();

	private RegisterUserRequest registerUserRequest;

	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {

	}

	@Test
	void testRegister() throws Exception {

		registerUserRequest = RegisterUserRequest.builder()
			.name("test")
			.birthday(LocalDate.now())
			.password("asdi2u34911!oj$@eI723")
			.contactNumber("010-0000-1111")
			.email("asd123@han.com")
			.loginId("asd123")
			.build();

		Mockito.doNothing().when(userService).requestRegister(any(RegisterUserRequest.class));

		String requestBody = mapper.writeValueAsString(registerUserRequest);

		mockMvc.perform(post("/api/account/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk());
	}
}
