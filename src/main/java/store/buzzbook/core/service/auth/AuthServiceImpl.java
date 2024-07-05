package store.buzzbook.core.service.auth;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.client.auth.AuthClient;
import store.buzzbook.core.common.exception.auth.AuthorizeFailException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
	private final AuthClient authClient;

	@Override
	public Map<String, Object> getInfoMapFromJwt(HttpServletRequest request) {
		String accessToken = request.getHeader(TOKEN_HEADER);
		String refreshToken = request.getHeader(REFRESH_HEADER);
		accessToken = wrapToken(accessToken);
		refreshToken = wrapToken(refreshToken);

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

	@Override
	public Long getUserIdFromJwt(HttpServletRequest request) {
		Map<String, Object> claims = getInfoMapFromJwt(request);
		return ((Integer)claims.get(AuthService.USER_ID)).longValue();
	}

	private String wrapToken(String token) {
		return String.format("Bearer %s", token);
	}
}
