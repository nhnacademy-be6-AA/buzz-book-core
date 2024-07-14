package store.buzzbook.core.aop;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.core.common.aop.UserJwtAop;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;
import store.buzzbook.core.service.auth.AuthService;

@ExtendWith(MockitoExtension.class)
class UserJwtAopTest {

	@Mock
	private AuthService authService;

	@Spy
	private HttpServletRequest request;

	@Mock
	private JoinPoint joinPoint;

	@InjectMocks
	private UserJwtAop userJwtAop;

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@Captor
	private ArgumentCaptor<Object> objectCaptor;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String VALID_TOKEN = "valid-jwt-token";
	private static final String INVALID_TOKEN = "invalid-jwt-token";

	private Map<String, Object> claims;

	@BeforeEach
	void setUp() {
		claims = new HashMap<>();
		claims.put(AuthService.USER_ID, 1);
		claims.put(AuthService.LOGIN_ID, "testLoginId");
		claims.put(AuthService.ROLE, "USER");
	}

	@Test
	void testAuthenticate_ValidToken() throws Throwable {
		Mockito.when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(VALID_TOKEN);
		Mockito.when(authService.getInfoMapFromJwt(request)).thenReturn(claims);

		userJwtAop.authenticate(joinPoint);

		Mockito.verify(request, Mockito.times(3)).setAttribute(stringCaptor.capture(), objectCaptor.capture());

		Assertions.assertEquals(AuthService.USER_ID, stringCaptor.getAllValues().get(0));
		Assertions.assertEquals(1L, objectCaptor.getAllValues().get(0));

		Assertions.assertEquals(AuthService.LOGIN_ID, stringCaptor.getAllValues().get(1));
		Assertions.assertEquals("testLoginId", objectCaptor.getAllValues().get(1));

		Assertions.assertEquals(AuthService.ROLE, stringCaptor.getAllValues().get(2));
		Assertions.assertEquals("USER", objectCaptor.getAllValues().get(2));
	}

	@Test
	void testAuthenticate_NoToken() {
		Mockito.when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

		Exception exception = Assertions.assertThrows(AuthorizeFailException.class, () -> {
			userJwtAop.authenticate(joinPoint);
		});

		Assertions.assertEquals("jwt token이 없습니다.", exception.getMessage());
		Mockito.verify(request, Mockito.times(1)).getHeader(AUTHORIZATION_HEADER);
	}

	@Test
	void testAuthenticate_InvalidToken() throws Throwable {
		Mockito.when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(INVALID_TOKEN);
		Mockito.when(authService.getInfoMapFromJwt(request)).thenReturn(new HashMap<>());

		Exception exception = Assertions.assertThrows(AuthorizeFailException.class, () -> {
			userJwtAop.authenticate(joinPoint);
		});

		Assertions.assertEquals("user info가 null입니다.", exception.getMessage());
		Mockito.verify(request, Mockito.times(1)).getHeader(AUTHORIZATION_HEADER);
		Mockito.verify(authService, Mockito.times(1)).getInfoMapFromJwt(request);
	}
}