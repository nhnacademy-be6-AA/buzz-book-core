package store.buzzbook.core.entity.user;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Builder
public class UserPk implements Serializable {
	public UserPk(long userId, String loginId) {
		this.id = userId;
		this.loginId = loginId;
	}

	@Column(name = "id")
	private Long id;

	@Column(name = "login_id")
	private String loginId;

}
