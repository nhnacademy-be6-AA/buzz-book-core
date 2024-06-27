package store.buzzbook.core.repository.user;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

}
