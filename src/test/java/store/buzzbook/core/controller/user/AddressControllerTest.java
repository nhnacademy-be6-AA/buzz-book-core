package store.buzzbook.core.controller.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.user.AddressInfoResponse;
import store.buzzbook.core.dto.user.CreateAddressRequest;
import store.buzzbook.core.dto.user.UpdateAddressRequest;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.user.AddressService;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AddressService addressService;

	@Autowired
	private ObjectMapper objectMapper;

	private Long userId;
	private Long addressId;
	private CreateAddressRequest createAddressRequest;
	private UpdateAddressRequest updateAddressRequest;
	private List<AddressInfoResponse> addressList;

	@BeforeEach
	void setUp() {
		userId = 1L;
		addressId = 1L;

		createAddressRequest = CreateAddressRequest.builder()
			.address("도로명주소")
			.alias("별칭")
			.nation("국가")
			.zipcode(12345)
			.detail("상세주소").build();

		updateAddressRequest = new UpdateAddressRequest(
			1L, "새로운 주소", "새로운 상세 주소",
			12344, "새 국가", "새 별칭"
		);

		addressList = List.of(new AddressInfoResponse(
			2L, createAddressRequest.address(),
			createAddressRequest.detail(),
			createAddressRequest.zipcode(),
			createAddressRequest.nation(),
			createAddressRequest.alias()
		), new AddressInfoResponse(
			updateAddressRequest.id(),
			updateAddressRequest.address(),
			updateAddressRequest.detail(),
			updateAddressRequest.zipcode(),
			updateAddressRequest.nation(),
			updateAddressRequest.alias()
		));
	}

	@Test
	@DisplayName("주소 생성 성공")
	void testCreateAddress() throws Exception {
		mockMvc.perform(post("/api/account/address")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createAddressRequest))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk());

		Mockito.verify(addressService, Mockito.times(1))
			.createAddress(Mockito.any(CreateAddressRequest.class), Mockito.eq(userId));
	}

	@Test
	@DisplayName("주소 삭제 성공")
	void testDeleteAddress() throws Exception {
		mockMvc.perform(delete("/api/account/address/{addressId}", addressId)
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk());

		Mockito.verify(addressService, Mockito.times(1)).deleteAddress(Mockito.eq(userId), Mockito.eq(addressId));
	}

	@Test
	@DisplayName("주소 수정 성공")
	void testUpdateAddress() throws Exception {
		mockMvc.perform(put("/api/account/address")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateAddressRequest))
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk());

		Mockito.verify(addressService, Mockito.times(1))
			.updateAddress(Mockito.any(UpdateAddressRequest.class), Mockito.eq(userId));
	}

	@Test
	@DisplayName("주소 가져오기 성공")
	void testGetAddressList() throws Exception {
		Mockito.when(addressService.getAddressList(userId)).thenReturn(addressList);

		mockMvc.perform(get("/api/account/address")
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(addressList.size()));

		Mockito.verify(addressService, Mockito.times(1)).getAddressList(Mockito.eq(userId));
	}

	@Test
	@DisplayName("주문용 주소 가져오기 성공")
	void testGetAddressListForOrder() throws Exception {
		Mockito.when(addressService.getAddressList(userId)).thenReturn(addressList);

		mockMvc.perform(get("/api/account/address/order")
				.with(request -> {
					request.setAttribute(AuthService.USER_ID, userId);
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(addressList.size()));

		Mockito.verify(addressService, Mockito.times(1)).getAddressList(Mockito.eq(userId));
	}
}