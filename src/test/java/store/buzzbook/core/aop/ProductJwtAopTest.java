package store.buzzbook.core.aop;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.core.common.aop.ProductJwtAop;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;
import store.buzzbook.core.service.auth.AuthService;

class ProductJwtAopTest {

	@Mock
	private AuthService authService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private JoinPoint joinPoint;

	@InjectMocks
	private ProductJwtAop productJwtAop;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("JWT 토큰이 없을 때 예외 발생 테스트")
	void testNoJwtToken() {
		// given
		when(request.getHeader("Authorization")).thenReturn(null);

		// when & then
		assertThrows(AuthorizeFailException.class, () -> productJwtAop.authenticate(joinPoint), "JWT 토큰이 없습니다.");
	}

	@Test
	@DisplayName("사용자 정보가 null일 때 예외 발생 테스트")
	void testUserInfoNull() {
		// given
		when(request.getHeader("Authorization")).thenReturn("Bearer token");
		when(authService.getInfoMapFromJwt(request)).thenReturn(new HashMap<>());

		// when & then
		assertThrows(AuthorizeFailException.class, () -> productJwtAop.authenticate(joinPoint), "사용자 정보가 null 입니다.");
	}

	@Test
	@DisplayName("접근 권한이 없을 때 예외 발생 테스트")
	void testAccessDenied() {
		// given
		when(request.getHeader("Authorization")).thenReturn("Bearer token");
		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthService.USER_ID, 1L);
		claims.put(AuthService.LOGIN_ID, "admin");
		claims.put(AuthService.ROLE, "user"); // 권한을 "user"로 설정
		when(authService.getInfoMapFromJwt(request)).thenReturn(claims);

		// when & then
		assertThrows(AuthorizeFailException.class, () -> productJwtAop.authenticate(joinPoint), "접근 권한이 없습니다.");
	}

	@Test
	@DisplayName("올바른 JWT 토큰일 때 요청 속성 설정 테스트")
	void testValidJwtToken() throws Throwable {
		// given
		when(request.getHeader("Authorization")).thenReturn("Bearer token");
		Map<String, Object> claims = new HashMap<>();
		claims.put(AuthService.USER_ID, 1L);
		claims.put(AuthService.LOGIN_ID, "admin");
		claims.put(AuthService.ROLE, "admin");
		when(authService.getInfoMapFromJwt(request)).thenReturn(claims);

		// when
		productJwtAop.authenticate(joinPoint);

		// then
		verify(request, times(1)).setAttribute(AuthService.USER_ID, 1L);
		verify(request, times(1)).setAttribute(AuthService.LOGIN_ID, "admin");
		verify(request, times(1)).setAttribute(AuthService.ROLE, "admin");
	}
}