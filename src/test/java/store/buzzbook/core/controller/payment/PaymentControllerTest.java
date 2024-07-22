package store.buzzbook.core.controller.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.buzzbook.core.common.listener.OrderStatusListener.*;

import java.time.LocalDate;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import store.buzzbook.core.dto.payment.CreateBillLogRequest;
import store.buzzbook.core.dto.payment.CreateCancelBillLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogRequest;
import store.buzzbook.core.dto.payment.ReadBillLogResponse;
import store.buzzbook.core.dto.payment.ReadBillLogWithoutOrderResponse;
import store.buzzbook.core.dto.payment.ReadBillLogsRequest;
import store.buzzbook.core.dto.payment.ReadPaymentKeyRequest;
import store.buzzbook.core.dto.payment.ReadPaymentResponse;
import store.buzzbook.core.dto.user.GradeInfoResponse;
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

	private UserInfo testUserInfo;
	private List<ReadBillLogWithoutOrderResponse> readBillLogWithoutOrderResponses = new ArrayList<>();
	private ReadBillLogWithoutOrderResponse readBillLogWithoutOrderResponse;
	private ReadBillLogRequest readBillLogRequest;
	private ReadPaymentKeyRequest readPaymentKeyRequest;
	private ReadPaymentKeyRequest readPaymentKeyRequest2;

	@BeforeEach
	public void setUp() {
		testUserInfo = new UserInfo(504L, "parkseol", "01011111111", "parkseol",
			"parkseol.dev@gmail.com", LocalDate.parse("2024-06-28"), GradeInfoResponse.builder().benefit(0.03)
			.name("PLATINUM").standard(300000).build(), true, 5000);

		readBillLogWithoutOrderResponse = new ReadBillLogWithoutOrderResponse(1L, "간편결제", 1000, LocalDateTime.now(),
			BillStatus.DONE, "tviva20240721175933qQnE4", null);
		readBillLogWithoutOrderResponses.add(readBillLogWithoutOrderResponse);
		readBillLogRequest = new ReadBillLogRequest("MC4zODU1NzE0MTc1NzQy");
		readPaymentKeyRequest = new ReadPaymentKeyRequest("MC4xMDE2OTQyNzMzOTg5", "parkseol.dev@gmail.com");
		readPaymentKeyRequest2 = new ReadPaymentKeyRequest("MC4xMDE2OTQyNzMzOTg5", null);
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

	@Test
	@DisplayName("포인트, 쿠폰 결제 내역 추가 - 로그인")
	void createBillLogForDifferentPayment_LoggedIn() throws Exception {
		CreateBillLogRequest createBillLogRequest = new CreateBillLogRequest();
		ReadBillLogResponse readBillLogResponse = new ReadBillLogResponse();

		when(paymentService.createBillLogWithDifferentPayment(any(CreateBillLogRequest.class), any(HttpServletRequest.class)))
			.thenReturn(readBillLogResponse);

		mockMvc.perform(post("/api/payments/bill-log/different-payment")
				.content(objectMapper.writeValueAsString(createBillLogRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.with(request -> {
					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(readBillLogResponse)));

		verify(paymentService).createBillLogWithDifferentPayment(any(CreateBillLogRequest.class), any(HttpServletRequest.class));
	}

	@Test
	@DisplayName("포인트, 쿠폰 취소 내역 추가 - 로그인")
	void createCancelBillLogForDifferentPayment_LoggedIn() throws Exception {
		CreateCancelBillLogRequest createCancelBillLogRequest = new CreateCancelBillLogRequest();

		mockMvc.perform(post("/api/payments/bill-log/different-payment/cancel")
				.content(objectMapper.writeValueAsString(createCancelBillLogRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.with(request -> {
					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(content().string("Canceled"));

		verify(paymentService).createCancelBillLogWithDifferentPayment(any(CreateCancelBillLogRequest.class), any(HttpServletRequest.class));
	}

	@Test
	@DisplayName("포인트, 쿠폰 환불 내역 추가 - 로그인")
	void createRefundBillLogForDifferentPayment_LoggedIn() throws Exception {
		CreateCancelBillLogRequest createCancelBillLogRequest = new CreateCancelBillLogRequest();

		mockMvc.perform(post("/api/payments/bill-log/different-payment/refund")
				.content(objectMapper.writeValueAsString(createCancelBillLogRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.with(request -> {
					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
					return request;
				}))
			.andExpect(status().isOk())
			.andExpect(content().string("Canceled"));

		verify(paymentService).createRefundBillLogWithDifferentPayment(any(CreateCancelBillLogRequest.class), any(HttpServletRequest.class));
	}

	// @Test
	// @DisplayName("결제키 조회 - 비회원")
	// void getPaymentKey_NotLoggedIn() throws Exception {
	// 	when(paymentService.getPaymentKeyWithoutLogin(readPaymentKeyRequest.getOrderId(), readPaymentKeyRequest.getOrderEmail()))
	// 		.thenReturn(anyString());
	//
	// 	mockMvc.perform(post("/api/payments/payment-key")
	// 			.content(objectMapper.writeValueAsString(readPaymentKeyRequest))
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(content().string(anyString()));
	//
	// 	verify(paymentService).getPaymentKeyWithoutLogin(anyString(), anyString());
	// }

	// @Test
	// @DisplayName("결제키 조회 - 회원")
	// void getPaymentKey_LoggedIn() throws Exception {
	// 	when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
	// 	when(paymentService.getPaymentKey(readPaymentKeyRequest2.getOrderId(), testUserInfo.id())).thenReturn(anyString());
	//
	// 	mockMvc.perform(post("/api/payments/payment-key")
	// 			.content(objectMapper.writeValueAsString(readPaymentKeyRequest))
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.requestAttr(AuthService.LOGIN_ID, "parkseol"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(content().string(anyString()));
	//
	// 	verify(paymentService).getPaymentKey(anyString(), anyLong());
	// }

	// @Test
	// @DisplayName("주문상세 아이디로 결제키 조회 - 비회원")
	// void getPaymentKeyWithOrderDetailId_NotLoggedIn() throws Exception {
	// 	ReadPaymentKeyWithOrderDetailRequest readPaymentKeyWithOrderDetailRequest = new ReadPaymentKeyWithOrderDetailRequest();
	// 	readPaymentKeyWithOrderDetailRequest.setOrderDetailId(123L);
	// 	readPaymentKeyWithOrderDetailRequest.setOrderEmail("test@example.com");
	//
	// 	when(orderService.readOrderStr(anyLong())).thenReturn("orderStr");
	// 	when(paymentService.getPaymentKeyWithoutLogin(anyString(), anyString()))
	// 		.thenReturn("responseKey");
	//
	// 	mockMvc.perform(post("/api/payments/detail/payment-key")
	// 			.content(objectMapper.writeValueAsString(readPaymentKeyWithOrderDetailRequest))
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(content().string("responseKey"));
	//
	// 	verify(paymentService).getPaymentKeyWithoutLogin(eq("orderStr"), eq("test@example.com"));
	// }

	// @Test
	// @DisplayName("주문상세 아이디로 결제키 조회 - 회원")
	// void getPaymentKeyWithOrderDetailId_LoggedIn() throws Exception {
	// 	ReadPaymentKeyWithOrderDetailRequest readPaymentKeyWithOrderDetailRequest = new ReadPaymentKeyWithOrderDetailRequest();
	// 	readPaymentKeyWithOrderDetailRequest.setOrderDetailId(123L);
	// 	UserInfo userInfo = new UserInfo();
	// 	userInfo.setId(1L);
	//
	// 	when(userService.getUserInfoByLoginId(anyString())).thenReturn(userInfo);
	// 	when(orderService.readOrderStr(anyLong())).thenReturn("orderStr");
	// 	when(paymentService.getPaymentKey(anyString(), anyLong())).thenReturn("responseKey");
	//
	// 	mockMvc.perform(post("/api/payments/detail/payment-key")
	// 			.content(objectMapper.writeValueAsString(readPaymentKeyWithOrderDetailRequest))
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.requestAttr(AuthService.LOGIN_ID, "testLoginId"))
	// 		.andExpect(status().isOk())
	// 		.andExpect(content().string("responseKey"));
	//
	// 	verify(paymentService).getPaymentKey(eq("orderStr"), eq(1L));
	// }
}
