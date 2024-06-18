package store.buzzbook.core.dto.user;

import java.time.ZonedDateTime;

import lombok.Builder;
import store.buzzbook.core.entity.user.Grade;

@Builder
public record UserInfo(long id, String loginId,
					   String contactNumber, String name, String email,
					   ZonedDateTime birthday, Grade grade, boolean isAdmin) {
}
