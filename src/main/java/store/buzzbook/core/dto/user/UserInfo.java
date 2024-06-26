package store.buzzbook.core.dto.user;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import lombok.Builder;
import store.buzzbook.core.entity.user.Grade;

@Builder
public record UserInfo(Long id, String loginId,
					   String contactNumber, String name, String email,
					   LocalDate birthday, Grade grade, boolean isAdmin) {
}
