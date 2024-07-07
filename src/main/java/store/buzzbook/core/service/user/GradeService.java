package store.buzzbook.core.service.user;

import store.buzzbook.core.entity.user.Grade;

/**
 * 회원 등급 관리를 위한 서비스입니다.
 * @author Heldenarr
 */
public interface GradeService {
	/**
	 * 등급 저장입니다. 이름이 중복될 경우 저장되지 않습니다.
	 *
	 * @author Heldenarr
	 * @param grade 저장할 등급 객체입니다.
	 *
	 */
	void save(Grade grade);

}
