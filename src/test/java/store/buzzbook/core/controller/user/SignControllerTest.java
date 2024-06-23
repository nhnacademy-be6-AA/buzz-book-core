// package store.buzzbook.core.controller.user;
//
// import static org.hamcrest.Matchers.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.time.ZonedDateTime;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import store.buzzbook.core.common.config.JacksonConfig;
// import store.buzzbook.core.common.exception.user.UserAlreadyExistsException;
// import store.buzzbook.core.common.util.ZonedDateTimeParser;
// import store.buzzbook.core.dto.user.RegisterUserRequest;
// import store.buzzbook.core.dto.user.RegisterUserResponse;
// import store.buzzbook.core.service.user.UserService;
//
// @WebMvcTest(SignController.class)
// @Import(JacksonConfig.class)
// @AutoConfigureMockMvc
// class SignControllerTest {
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper mapper = new ObjectMapper();
//
// 	private RegisterUserRequest registerUserRequest;
//
// 	@MockBean
// 	private UserService userService;
//
// 	@BeforeEach
// 	void setUp() {
//
// 	}
//
// 	@Test
// 	void testRegister() throws Exception {
//
// 		registerUserRequest = RegisterUserRequest.builder()
// 			.name("test")
// 			.birthday(ZonedDateTimeParser.toStringDate(ZonedDateTime.now()))
// 			.password("asdi2u34911!oj$@eI723")
// 			.contactNumber("010-0000-1111")
// 			.email("asd123@han.com")
// 			.loginId("asd123")
// 			.build();
//
// 		Mockito.when(userService.requestRegister(any(RegisterUserRequest.class)))
// 			.thenAnswer(invocation -> {
// 				String loginId = ((RegisterUserRequest)invocation.getArgument(0)).loginId();
//
// 				if (loginId.equals("duplicated")) {
// 					throw new UserAlreadyExistsException(loginId);
// 				}
//
// 				return RegisterUserResponse.builder()
// 					.loginId(registerUserRequest.loginId())
// 					.name(registerUserRequest.name())
// 					.message("회원가입 성공")
// 					.status(HttpStatus.OK.value()).build();
// 			});
//
// 		String requestBody = mapper.writeValueAsString(registerUserRequest);
//
// 		mockMvc.perform(post("/api/account/register")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(requestBody))
// 			.andExpect(status().isOk())
// 			.andExpect(content().string(containsString(registerUserRequest.name())))
// 			.andExpect(content().string(containsString(registerUserRequest.loginId())));
// 	}
// }
