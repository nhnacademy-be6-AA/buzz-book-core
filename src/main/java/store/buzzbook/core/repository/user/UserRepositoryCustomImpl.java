package store.buzzbook.core.repository.user;

import static store.buzzbook.core.entity.user.QUser.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.user.UserInfo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<UserInfo> findAllByBirthday(UserInfo userInfo) {

		return jpaQueryFactory
			.select(Projections.fields(UserInfo.class))
			.from(user)
			.where(
				user.birthday.month().eq(LocalDate.now().getMonthValue())
			).fetch();
	}
}