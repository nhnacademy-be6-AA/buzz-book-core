package store.buzzbook.core.controller.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.payment.ReadBillLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.dto.payment.ReadPaymentResponse;
import store.buzzbook.core.dto.user.UserInfo;
import store.buzzbook.core.entity.payment.BillStatus;
import store.buzzbook.core.service.auth.AuthService;
import store.buzzbook.core.service.order.OrderService;
import store.buzzbook.core.service.payment.PaymentService;
import store.buzzbook.core.service.user.UserService;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private PaymentService paymentService;

	@MockBean
	private OrderService orderService;

	@MockBean
	private UserService userService;

	private List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderResponses = new ArrayList<>();
	private ReadBillLogWithoutOrderResponse readBillLogWithoutOrderResponse;
	private ReadBillLogRequest readBillLogRequest;

	@BeforeEach
	public void setUp() {
		readBillLogWithoutOrderResponse = new ReadBillLogWithoutOrderResponse(1L, "간편결제", 1000, LocalDateTime.now(),
			BillStatus.DONE, "tviva20240721175933qQnE4", null);
		readBillLogWithoutOrderResponses.add(readBillLogWithoutOrderResponse);
		readBillLogRequest = new ReadBillLogRequest("MC4zODU1NzE0MTc1NzQy");
	}

	@Test
	@DisplayName("주문 하나에 딸린 결제 내역들 조회 - 비회원")
	void getBillLogs_NotLoggedIn() throws Exception {
		when(paymentService.readBillLogWithoutOrderWithoutLogin(readBillLogRequest.getOrderId()))
			.thenReturn(readBillLogWithoutOrderResponses);

		mockMvc.perform(post("/api/payments/bill-logs")
				.content(objectMapper.writeValueAsString(readBillLogRequest))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(paymentService).readBillLogWithoutOrderWithoutLogin(anyString());
	}

	@Test
	@DisplayName("주문 하나에 딸린 결제 내역들 조회 - 관리자")
	void getBillLogs_Admin() throws Exception {
		UserInfo userInfo = mock(UserInfo.class);

		when(userInfo.isAdmin()).thenReturn(true);
		when(userService.getUserInfoByLoginId(anyString())).thenReturn(userInfo);
		when(paymentService.readBillLogWithoutOrderWithAdmin(readBillLogRequest.getOrderId()))
			.thenReturn(List.of(new ReadBillLogWithoutOrderResponse()));

		mockMvc.perform(post("/api/payments/bill-logs")
				.content(objectMapper.writeValueAsString(readBillLogRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.with(request -> {
					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
					return request;
				}))
			.andExpect(status().isOk());

		verify(paymentService).readBillLogWithoutOrderWithAdmin(anyString());
	}

	@Test
	@DisplayName("관리자의 결제 내역 모두 조회")
	void getAllBillLogs_AdminUser() throws Exception {
		ReadBillLogsRequest readBillLogsRequest = new ReadBillLogsRequest();
		UserInfo userInfo = mock(UserInfo.class);

		when(userInfo.isAdmin()).thenReturn(true);
		when(userService.getUserInfoByLoginId(anyString())).thenReturn(userInfo);
		when(paymentService.readBillLogs(any(ReadBillLogsRequest.class)))
			.thenReturn(Map.of("responseData", List.of(new ReadBillLogResponse())));

		mockMvc.perform(post("/api/payments/admin/bill-logs")
				.content(objectMapper.writeValueAsString(readBillLogsRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.requestAttr(AuthService.LOGIN_ID, "testLoginId"))
			.andExpect(status().isOk());

		verify(paymentService).readBillLogs(any(ReadBillLogsRequest.class));
	}

	@Test
	@DisplayName("결제 내역 추가")
	void createBillLog() throws Exception {
		JSONObject createBillLogRequest = new JSONObject();
		ReadBillLogResponse expectedResponse = new ReadBillLogResponse();

		when(paymentService.createBillLog(any(JSONObject.class))).thenReturn(expectedResponse);

		mockMvc.perform(post("/api/payments/bill-log")
				.content(createBillLogRequest.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(paymentService).createBillLog(any(JSONObject.class));
	}

	@Test
	@DisplayName("결제 내역 추가 실패 시 롤백")
	void rollbackBillLog() throws Exception {
		ReadPaymentResponse readPaymentResponse = new ReadPaymentResponse();

		mockMvc.perform(post("/api/payments/bill-log/rollback")
				.content(objectMapper.writeValueAsString(readPaymentResponse))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(paymentService).rollbackBillLog(any(ReadPaymentResponse.class));
	}

	@Test
	@DisplayName("취소 내역 추가")
	void createCancelBillLog() throws Exception {
		JSONObject createBillLogRequest = new JSONObject();
		ReadBillLogResponse expectedResponse = new ReadBillLogResponse();

		when(paymentService.createCancelBillLog(any(JSONObject.class))).thenReturn(expectedResponse);

		mockMvc.perform(post("/api/payments/bill-log/cancel")
				.content(createBillLogRequest.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(paymentService).createCancelBillLog(any(JSONObject.class));
	}
}
