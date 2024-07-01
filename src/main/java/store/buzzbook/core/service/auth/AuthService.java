package store.buzzbook.core.service.auth;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
	String TOKEN_HEADER = "Authorization";
	String REFRESH_HEADER = "Refresh-Token";
	String MESSAGE = "message";
	String ERROR = "error";
	String USER_ID = "user_id";

	Long getUserId(HttpServletRequest request);
}
