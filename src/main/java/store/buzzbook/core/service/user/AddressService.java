package store.buzzbook.core.service.user;

import java.util.List;

import store.buzzbook.core.common.exception.user.AddressMaxCountException;
import store.buzzbook.core.common.exception.user.UserNotFoundException;
import store.buzzbook.core.dto.user.AddressInfoResponse;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;

/**
 * 사용자 주소를 관리하기 위한 서비스.
 */
public interface AddressService {
	/**
	 * 제공된 주소 정보와 사용자 ID를 사용하여 새로운 주소를 생성합니다. 최대 10개까지 생성 가능합니다.
	 *
	 * @param createAddressRequest 주소 생성 세부 정보를 포함하는 요청 객체. 도로명주소, 상세주소, 우편번호, 별칭 등이 담겨있습니다.
	 * @param userId 주소를 생성할 사용자의 ID
	 * @throws UserNotFoundException 존재하지 않는 회원의 주소 생성 요청
	 * @throws AddressMaxCountException 해당 회원의 저장된 주소 갯수가 한계를 초과했습니다.
	 */
	void createAddress(CreateAddressRequest createAddressRequest, long userId);

	/**
	 * 주어진 주소 ID와 사용자 ID를 사용하여 주소를 삭제합니다.
	 *
	 * @param addressId 삭제할 주소의 ID
	 * @param userId 주소를 삭제할 사용자의 ID
	 * @throws UserNotFoundException 존재하지 않는 회원의 주소 삭제 요청
	 */
	void deleteAddress(Long addressId, Long userId);

	/**
	 * 제공된 세부 정보와 사용자 ID를 사용하여 주소를 업데이트합니다.
	 *
	 * @param updateAddressRequest 주소 수정 세부 정보를 포함하는 요청 객체
	 * @param userId 주소를 업데이트할 사용자의 ID
	 * @throws UserNotFoundException 존재하지 않는 회원의 주소 수정 요청      
	 */
	void updateAddress(UpdateAddressRequest updateAddressRequest, long userId);

	/**
	 * 주어진 사용자 ID로 주소 목록을 조회합니다. 최대 10개까지 조회합니다.
	 *
	 * @param userId 주소 목록을 조회할 사용자의 ID
	 * @return 주소 정보를 포함하는 AddressInfoResponse 객체의 List
	 * @throws UserNotFoundException 존재하지 않는 회원의 주소 조회 요청
	 */
	List<AddressInfoResponse> getAddressList(Long userId);

}
