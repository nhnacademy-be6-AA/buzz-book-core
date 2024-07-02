package store.buzzbook.core.service.auth;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
	String TOKEN_HEADER = "Authorization";
	String REFRESH_HEADER = "Refresh-Token";
	String MESSAGE = "message";
	String ERROR = "error";
	String USER_ID = "userId";
	String ROLE = "role";
	String LOGIN_ID = "loginId";

	Map<String, Object> getInfoMapFromJwt(HttpServletRequest request);

	Long getUserIdFromJwt(HttpServletRequest request);
}
