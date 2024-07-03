package store.buzzbook.core.common.aop;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.service.auth.AuthService;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OrderJwtAop {
	private final AuthService authService;
	private final HttpServletRequest request;


}
