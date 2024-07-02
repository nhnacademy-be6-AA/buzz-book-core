package store.buzzbook.core.common.aop;

import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;
import store.buzzbook.core.common.exception.cart.InvalidCartUuidException;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.cart.CartService;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CartJwtAop {
	private final CartService cartService;
	private final AuthService authService;
	private final HttpServletRequest request;

	@Before("execution(* store.buzzbook.core.controller.cart.CartController.*(..)) && args(uuid,..)")
	public void authenticate(JoinPoint joinPoint, String uuid) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");

		//비회원 -- uuid 체크 (존재유무로 위변조체크)
		if (Objects.isNull(authorizationHeader) && !cartService.isValidUUID(uuid)) {
			throw new InvalidCartUuidException();
		} else if (Objects.nonNull(authorizationHeader)) {
			//회원
			Long userId = authService.getUserIdFromJwt(request);
			request.setAttribute(AuthService.USER_ID, userId);
		}
	}

	@Before("execution(* store.buzzbook.core.controller.cart.CartController.getUuidByUserId(..))")
	public void getUuidByUserId(JoinPoint joinPoint) throws Throwable {
		String authorizationHeader = request.getHeader("Authorization");
		//비회원 -- getUuidByUserId는 회원만 가능
		if (Objects.isNull(authorizationHeader)) {
			throw new AuthorizeFailException("Authorization header is missing");
		}

		//회원
		Long userId = authService.getUserIdFromJwt(request);
		request.setAttribute("userId", userId);
	}

}
