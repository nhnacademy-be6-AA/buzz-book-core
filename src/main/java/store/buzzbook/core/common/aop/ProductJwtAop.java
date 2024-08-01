package store.buzzbook.core.common.aop;

import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;
import store.buzzbook.core.service.auth.AuthService;

@Component
@RequiredArgsConstructor
@Aspect
public class ProductJwtAop {
	private final AuthService authService;
	private final HttpServletRequest request;

	//Product AdminPage jwt 토큰
	@Before("@annotation(store.buzzbook.core.common.annotation.JwtAdminValidate)")
	public void authenticate(JoinPoint joinPoint) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");

		if (Objects.isNull(authorizationHeader)) {
			throw new AuthorizeFailException("JWT 토큰이 없습니다.");
		}

		Map<String, Object> claims = authService.getInfoMapFromJwt(request);

		try {
		Long userId = (Long) claims.get(AuthService.USER_ID);
		String loginId = (String) claims.get(AuthService.LOGIN_ID);
		String role = (String) claims.get(AuthService.ROLE);

			if (!"admin".equals(role)) {
				throw new AuthorizeFailException("접근 권한이 없습니다.");
			}

			request.setAttribute(AuthService.USER_ID, userId);
			request.setAttribute(AuthService.LOGIN_ID, loginId);
			request.setAttribute(AuthService.ROLE, role);

		}catch (NullPointerException e) {
			throw new AuthorizeFailException("user info가 null입니다.");
		}
	}

}
