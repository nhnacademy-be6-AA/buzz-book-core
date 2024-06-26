package store.buzzbook.core.entity.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.ColumnDefault;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.buzzbook.core.dto.user.UserInfo;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "`user`")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "user")
	private List<UserCoupon> userCoupons = new ArrayList<>();

	@NotNull
	@Size(min = 6, max = 20)
	@Column(name = "login_id", unique = true)
	private String loginId;

	@NotNull
	@Size(min = 6, max = 15)
	@Column(name = "contact_number")
	private String contactNumber;

	@NotNull
	@Size(max = 20)
	private String name;

	@NotNull
	@Size(max = 255)
	@Email
	private String email;

	@NotNull
	@Size(max = 255)
	private String password;

	@NotNull
	@Past
	private LocalDate birthday;

	@NotNull
	@Column(name = "created_at")
	@Past
	private LocalDateTime createAt;

	@NotNull
	@Column(name = "last_login_at")
	@Past
	private LocalDateTime lastLoginAt;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private UserStatus status;

	@NotNull
	@Column(name = "modify_at")
	@Past
	private LocalDateTime modifyAt;

	@NotNull
	@Column(name = "is_admin")
	@ColumnDefault("false")
	private boolean isAdmin;

	public void deactivate() {
		this.status = UserStatus.WITHDRAW;
	}

	public void activate() {
		this.status = UserStatus.ACTIVE;
	}

	public void updateLastLogin() {
		this.lastLoginAt = LocalDateTime.now();
	}

	public UserInfo toUserInfo(Grade grade) {
		return UserInfo.builder()
			.email(this.email)
			.id(this.id)
			.contactNumber(this.contactNumber)
			.isAdmin(this.isAdmin)
			.birthday(this.birthday)
			.grade(grade)
			.loginId(this.loginId)
			.name(this.name).build();
	}

	public void updateLastLoginAt() {
	}
}
