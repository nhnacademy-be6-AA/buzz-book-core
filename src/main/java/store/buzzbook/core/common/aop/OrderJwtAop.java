package store.buzzbook.core.common.aop;

import static store.buzzbook.core.service.auth.AuthService.*;

import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.client.auth.AuthClient;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;
import store.buzzbook.core.service.auth.AuthService;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class OrderJwtAop {
	private final AuthClient authClient;
	private final HttpServletRequest request;

	@Before("@annotation(store.buzzbook.core.common.annotation.JwtOrderValidate)")
	public void authenticate(JoinPoint joinPoint) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");

		if (Objects.nonNull(authorizationHeader)) {
			Map<String, Object> claims = getInfoMapFromJwtForRestTemplate(request);

			for (String key : claims.keySet()) {
				claims.put(key, claims.get(key));
			}

			Long userId = ((Integer)claims.get(AuthService.USER_ID)).longValue();
			String loginId = (String)claims.get(AuthService.LOGIN_ID);
			String role = (String)claims.get(AuthService.ROLE);

			if (Objects.isNull(userId) || Objects.isNull(loginId) || Objects.isNull(role)) {
				throw new AuthorizeFailException("user info가 null입니다.");
			}

			request.setAttribute(AuthService.USER_ID, userId);
			request.setAttribute(AuthService.LOGIN_ID, loginId);
			request.setAttribute(AuthService.ROLE, role);
		}
	}

	@Before("@annotation(store.buzzbook.core.common.annotation.JwtOrderAdminValidate)")
	public void authenticateAdmin(JoinPoint joinPoint) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");

		if (Objects.isNull(authorizationHeader)) {
			throw new AuthorizeFailException("JWT 토큰이 없습니다.");
		}

		Map<String, Object> claims = getInfoMapFromJwtForRestTemplate(request);

		Long userId = ((Integer)claims.get(AuthService.USER_ID)).longValue();
		String loginId = (String) claims.get(AuthService.LOGIN_ID);
		String role = (String) claims.get(AuthService.ROLE);

		if (Objects.isNull(userId) || Objects.isNull(loginId) || Objects.isNull(role)) {
			throw new AuthorizeFailException("사용자 정보가 null 입니다.");
		}

		if (!"ADMIN".equals(role)) {
			throw new AuthorizeFailException("접근 권한이 없습니다.");
		}

		request.setAttribute(AuthService.USER_ID, userId);
		request.setAttribute(AuthService.LOGIN_ID, loginId);
		request.setAttribute(AuthService.ROLE, role);
	}

	private Map<String, Object> getInfoMapFromJwtForRestTemplate(HttpServletRequest request) {
		String accessToken = request.getHeader(TOKEN_HEADER);
		String refreshToken = request.getHeader(REFRESH_HEADER);

		ResponseEntity<Map<String, Object>> responseEntity = authClient.getUserInfo(accessToken, refreshToken);

		if (Objects.isNull(responseEntity.getBody())) {
			log.debug("토큰 인증에 실패 했습니다. : null point exception");
			throw new AuthorizeFailException("Invalid access token");
		}

		if (responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
			throw new AuthorizeFailException((String)responseEntity.getBody().get(ERROR),
				(String)responseEntity.getBody().get(MESSAGE));
		}

		return responseEntity.getBody();
	}
}
