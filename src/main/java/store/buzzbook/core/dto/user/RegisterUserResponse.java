package store.buzzbook.core.dto.user;

import lombok.Builder;

@Builder
public record RegisterUserResponse(
        int status,
        String name,
        String loginId,
        String message
) {
}
