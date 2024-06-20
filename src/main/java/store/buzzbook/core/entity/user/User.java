package store.buzzbook.core.entity.user;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Column(name = "login_id")
	private String loginId;
	@Column(name = "contact_number", nullable = false)
	private String contactNumber;

	@NotNull
	private String name;

	@NotNull
	private String email;

	@NotNull
	private String password;

	@NotNull
	private ZonedDateTime birthday;

	@NotNull
	@Column(name = "create_date")
	private ZonedDateTime createDate;

	@NotNull
	@Column(name = "last_login_date")
	private ZonedDateTime lastLoginDate;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private UserStatus status;

	@NotNull
	@Column(name = "modify_date")
	private ZonedDateTime modifyDate;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grade_id")
	private Grade grade;

	@NotNull
	@Column(name = "is_admin")
	private boolean isAdmin;

}
