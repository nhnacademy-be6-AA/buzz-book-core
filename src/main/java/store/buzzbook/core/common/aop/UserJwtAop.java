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

@Aspect
@RequiredArgsConstructor
@Component
public class UserJwtAop {
	private final AuthService authService;
	private final HttpServletRequest request;

	@Before("@annotation(store.buzzbook.core.common.annotation.JwtValidate)")
	public void authenticate(JoinPoint joinPoint) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");

		//비회원 --  체크 (존재유무로 위변조체크)
		if (Objects.isNull(authorizationHeader)) {
			throw new AuthorizeFailException("jwt token이 없습니다.");
		}
		//회원
		Map<String, Object> claims = authService.getInfoMapFromJwt(request);

		try {
			Long userId = ((Integer)claims.get(AuthService.USER_ID)).longValue();
			String loginId = (String)claims.get(AuthService.LOGIN_ID);
			String role = (String)claims.get(AuthService.ROLE);

			request.setAttribute(AuthService.USER_ID, userId);
			request.setAttribute(AuthService.LOGIN_ID, loginId);
			request.setAttribute(AuthService.ROLE, role);
			
		} catch (NullPointerException e) {
			throw new AuthorizeFailException("user info가 null입니다.");
		}
	}
}
