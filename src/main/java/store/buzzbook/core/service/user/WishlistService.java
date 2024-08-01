package store.buzzbook.core.service.user;

import org.springframework.data.domain.Page;

import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.product.ProductResponse;

/**
 * 사용자의 찜목록을 관리하기 위한 서비스
 */
public interface WishlistService {
	/**
	 * 찜목록 등록합니다.
	 *
	 * @param userId 찜목록을 등록할 사용자의 ID
	 * @param productId 찜목록을 등록할 상품의 ID
	 *
	 * @throws UserNotFoundException 존재하지 않는 사용자의 ID로 찜목록 등록 요청
	 * @throws DataNotFoundException 존재하지 않는 상품 ID로 찜목록 등록 요청
	 */
	long createWishlist(long userId, int productId);

	/**
	 * 찜목록을 삭제합니다.
	 *
	 * @param userId 사용자의 ID
	 * @param productId 상품의 ID
	 */
	void deleteWishlist(long userId, int productId);

	/**
	 * 주어진 사용자의 ID로 찜목록에 등록된 상품들을 반환
	 *
	 * @param userId 찜목록을 조회할 사용자의 ID
	 * @param pageNo 반환할 상품의 페이지 번호
	 * @param pageSize 반환할 상품의 페이지네이션 크기
	 *
	 * @return 페이지네이션된 상품 정보를 포함하는 응답 DTO
	 */
	Page<ProductResponse> getWishlists(long userId, int pageNo, int pageSize);

	/**
	 *
	 * @param userId 해당 상품을 찜목록에 등록했는지 확인할 사용자 ID
	 * @param productId 로그인한 사용자가 찜목록에 등록했는지 확인할 상품 ID
	 *
     * @throws UserNotFoundException 존재하지 않는 사용자의 ID로 찜목록 등록 요청
	 * @throws DataNotFoundException 존재하지 않는 상품 ID로 찜목록 등록 요청
	 *
	 * @return 등록되어있으면 해당 wishlist의 id값, 등록되어있지 않으면 null 반환
	 */
	Long isUserWishlist(long userId, int productId);
}
