package store.buzzbook.core.dto.user;

import lombok.Builder;

@Builder
public record LoginUserResponse(String loginId, String password) {
}
