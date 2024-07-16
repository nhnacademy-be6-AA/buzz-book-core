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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.user.UserRealBill;
import store.buzzbook.core.dto.user.UserRealBillInfo;
import store.buzzbook.core.dto.user.UserRealBillInfoDetail;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.service.user.UserService;

@WebMvcTest(UserBatchController.class)
class UserBatchControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	private List<UserRealBill> userRealBills;
	private String realBillListJson;

	@BeforeEach
	void setUp() throws JsonProcessingException {

		UserRealBillInfoDetail detail = UserRealBillInfoDetail.builder()
			.status(BillStatus.DONE)
			.price(10000).build();

		UserRealBillInfo info = UserRealBillInfo.builder()
			.deliveryRate(100)
			.detailList(List.of(detail)).build();

		userRealBills = List.of(UserRealBill.builder()
			.userId(1L)
			.userRealBillInfoList(List.of(info)).build());
		realBillListJson = objectMapper.writeValueAsString(userRealBills);
	}

	@Test
	@DisplayName("배치서버용 3달치 순수금액 결제 내역 얻기 성공")
	void testGet3MonthBills() throws Exception {
		Mockito.when(userService.getUserRealBills()).thenReturn(userRealBills);

		mockMvc.perform(get("/api/account/bills/3month")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(userRealBills.size()))
			.andExpect(content().json(realBillListJson));

		Mockito.verify(userService, Mockito.times(1)).getUserRealBills();
	}
}
