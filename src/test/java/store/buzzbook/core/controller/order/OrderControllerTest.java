// package store.buzzbook.core.controller.order;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockHttpServletRequest;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import store.buzzbook.core.dto.order.*;
// import store.buzzbook.core.dto.point.PointLogResponse;
// import store.buzzbook.core.dto.product.CategoryResponse;
// import store.buzzbook.core.dto.product.ProductResponse;
// import store.buzzbook.core.dto.product.TagResponse;
// import store.buzzbook.core.dto.user.GradeInfoResponse;
// import store.buzzbook.core.dto.user.RegisterUserRequest;
// import store.buzzbook.core.dto.user.UserInfo;
// import store.buzzbook.core.entity.product.Product;
// import store.buzzbook.core.entity.user.User;
// import store.buzzbook.core.entity.user.UserStatus;
// import store.buzzbook.core.service.auth.AuthService;
// import store.buzzbook.core.service.order.OrderService;
// import store.buzzbook.core.service.user.UserService;
//
// @WebMvcTest(OrderController.class)
// class OrderControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	private OrderService orderService;
//
// 	@MockBean
// 	private UserService userService;
//
// 	private UserInfo testUserInfo;
// 	private UserInfo testUserInfo2;
// 	private ReadOrdersRequest readOrdersRequest;
// 	private CreateOrderRequest createOrderRequest;
// 	private UpdateOrderRequest updateOrderRequest;
// 	private CreateOrderDetailRequest createOrderDetailRequest1;
// 	private CreateOrderDetailRequest createOrderDetailRequest2;
// 	private ReadOrderResponse readOrderResponse;
// 	private List<CreateOrderDetailRequest> createOrderDetailRequests;
// 	private List<ReadOrderDetailResponse> readOrderDetailResponses;
// 	private ReadOrderDetailResponse readOrderDetailResponse1;
// 	private ReadOrderDetailResponse readOrderDetailResponse2;
// 	private ReadOrderStatusResponse readOrderStatusResponse;
// 	private ReadWrappingResponse readWrappingResponse1;
// 	private ReadWrappingResponse readWrappingResponse2;
// 	private ProductResponse productResponse1;
// 	private ProductResponse productResponse2;
// 	private CategoryResponse categoryResponse;
// 	private LinkedHashMap<Integer, String> parentCategory = new LinkedHashMap<>();
// 	private List<TagResponse> tags = new ArrayList<>();
// 	private TagResponse tagResponse;
// 	private ReadOrdersResponse readOrdersResponse;
// 	private ReadOrderDetailProjectionResponse readOrderDetailProjectionResponse1;
// 	private ReadOrderDetailProjectionResponse readOrderDetailProjectionResponse2;
// 	private List<ReadOrderDetailProjectionResponse> readOrderDetailProjectionResponses;
// 	private RegisterUserRequest registerUserRequest;
// 	private User user;
// 	private PointLogResponse pointLogResponse;
// 	private List<ReadOrderStatusResponse> readOrderStatusResponses = new ArrayList<>();
// 	private ReadDeliveryPolicyResponse readDeliveryPolicyResponse;
//
// 	@BeforeEach
// 	void setUp() {
// 		registerUserRequest = RegisterUserRequest.builder()
// 			.name("parkseol")
// 			.birthday(LocalDate.parse("2024-06-28"))
// 			.password("asdi2u34911!oj$@eI723")
// 			.contactNumber("01011111111")
// 			.email("parkseol.dev@gmail.com")
// 			.loginId("parkseol")
// 			.build();
//
// 		testUserInfo = new UserInfo(504L, "parkseol", "01011111111", "parkseol",
// 			"parkseol.dev@gmail.com", LocalDate.parse("2024-06-28"), GradeInfoResponse.builder().benefit(0.03)
// 			.name("PLATINUM").standard(300000).build(), true, 5000);
//
// 		testUserInfo2 = new UserInfo(503L, "park", "01011111111", "park",
// 			"parkseol.dev@gmail.com", LocalDate.parse("2024-06-28"), GradeInfoResponse.builder().benefit(0.03)
// 			.name("PLATINUM").standard(300000).build(), false, 5000);
//
// 		createOrderDetailRequest1 = new CreateOrderDetailRequest(
// 			5000,                          // price
// 			2,                           // quantity
// 			true,                        // wrap
// 			LocalDateTime.now(),         // createDate
// 			1,                           // orderStatusId
// 			3,                        // wrappingId (nullable)
// 			1L,                     // orderId
// 			1,                         // productId
// 			"Product A",                 // productName
// 			"/images/productA.jpg",      // thumbnailPath
// 			null                 // couponCode (nullable)
// 		);
// 		createOrderDetailRequest2 = new CreateOrderDetailRequest(
// 			5000,                          // price
// 			1,                           // quantity
// 			true,                        // wrap
// 			LocalDateTime.now(),         // createDate
// 			1,                           // orderStatusId
// 			2,                        // wrappingId (nullable)
// 			1L,                     // orderId
// 			2,                         // productId
// 			"Product A",                 // productName
// 			"/images/productA.jpg",      // thumbnailPath
// 			null                 // couponCode (nullable)
// 		);
// 		createOrderDetailRequests = Arrays.asList(createOrderDetailRequest1, createOrderDetailRequest2);
// 		readOrdersRequest = new ReadOrdersRequest(1, 10);
// 		createOrderRequest = new CreateOrderRequest(
// 			"MC4wOTA4MjAzNjg2OTQy",        // orderStr
// 			15500,                      // price
// 			"Please deliver quickly", // request (nullable)
// 			"1",            // addresses
// 			"123 Main St",            // address
// 			"Apt 4B",                 // addressDetail
// 			12345,                    // zipcode
// 			"2024-07-15",             // desiredDeliveryDate
// 			"John Doe",               // receiver
// 			1,                        // deliveryPolicyId (nullable)
// 			"parkseol",         // loginId
// 			createOrderDetailRequests,                  // details
// 			"123-456-7890",           // contactNumber
// 			1,                        // orderStatusId (nullable)
// 			"Jane Doe",               // sender
// 			"098-765-4321",           // receiverContactNumber
// 			"parkseol.dev@gmail.com",   // orderEmail
// 			500,                      // myPoint (nullable)
// 			null,           // couponCode (nullable)
// 			0,                        // deliveryRate
// 			0
// 		);
//
// 		updateOrderRequest = new UpdateOrderRequest("MC4wOTA4MjAzNjg2OTQy", "REFUND");
//
// 		readOrderStatusResponse = new ReadOrderStatusResponse(4, "PAID", "2024-07-15 12:01:11");
// 		readWrappingResponse1 = new ReadWrappingResponse(1, "선물포장", 1000, false);
// 		readWrappingResponse2 = new ReadWrappingResponse(2, "신문지", 0, false);
//
// 		parentCategory = new LinkedHashMap<>();
// 		parentCategory.put(0, "전체");
// 		categoryResponse = new CategoryResponse(1, "국내도서", parentCategory, null);
// 		tagResponse = new TagResponse(1, "포장가능");
// 		tags.add(0, tagResponse);
// 		productResponse1 = new ProductResponse(1, 10, "리눅스 구조", "description",
// 			5000, LocalDate.now(), 3, "/images/productA.jpg", Product.StockStatus.SALE,
// 			categoryResponse, tags);
// 		productResponse2 = new ProductResponse(2, 10, "알고리즘 코딩테스트", "description",
// 			5000, LocalDate.now(), 3, "/images/productA.jpg", Product.StockStatus.SALE,
// 			categoryResponse, tags);
//
// 		readOrderDetailResponse1 = new ReadOrderDetailResponse(
// 			1L,                            // id
// 			5000,                           // price
// 			2,                             // quantity
// 			true,                          // wrap
// 			"2024-07-10T14:48:00Z",        // createdAt
// 			readOrderStatusResponse,           // readOrderStatusResponse
// 			readWrappingResponse1,              // readWrappingResponse
// 			productResponse1,               // productResponse
// 			"2024-07-12T16:00:00Z"         // updatedAt
// 		);
//
// 		readOrderDetailResponse2 = new ReadOrderDetailResponse(
// 			1L,                            // id
// 			5000,                           // price
// 			2,                             // quantity
// 			true,                          // wrap
// 			"2024-07-10T14:48:00Z",        // createdAt
// 			readOrderStatusResponse,           // readOrderStatusResponse
// 			readWrappingResponse2,              // readWrappingResponse
// 			productResponse2,               // productResponse
// 			"2024-07-12T16:00:00Z"         // updatedAt
// 		);
//
// 		readOrderDetailResponses = Arrays.asList(readOrderDetailResponse1, readOrderDetailResponse2);
//
// 		readOrderResponse = new ReadOrderResponse(
// 			1L,                    // id
// 			"MC4wOTA4MjAzNjg2OTQy",                 // orderStr
// 			"testUser",              // loginId
// 			15500,                        // price
// 			"Please deliver quickly.",  // request
// 			"123 Main St",              // address
// 			"Apt 4B",                   // addressDetail
// 			12345,                      // zipcode
// 			"2024-07-15",               // desiredDeliveryDate
// 			"John Doe",                 // receiver
// 			readOrderDetailResponses,                    // details
// 			"Jane Smith",               // sender
// 			"098-765-4321",                 // receiverContactNumber
// 			"123-456-7890",                 // senderContactNumber
// 			"parkseol.dev@gmail.com",   // orderEmail
// 			null,                   // couponCode
// 			0                          // deliveryRate
// 		);
//
// 		readOrderDetailProjectionResponse1 = new ReadOrderDetailProjectionResponse(1L, 5000, 2, "true", LocalDateTime.now(),
// 			"PAID", "선물포장", "리눅스 구조", LocalDateTime.now());
// 		readOrderDetailProjectionResponse2 = new ReadOrderDetailProjectionResponse(2L, 5000, 2, "true", LocalDateTime.now(),
// 			"PAID", "신문지", "알고리즘 코딩테스트", LocalDateTime.now());
// 		readOrderDetailProjectionResponses = Arrays.asList(readOrderDetailProjectionResponse1, readOrderDetailProjectionResponse2);
//
// 		readOrdersResponse = new ReadOrdersResponse(1L, "MC4wOTA4MjAzNjg2OTQy", "parkseol", 15500, "Please deliver quickly.",
// 			"123 Main St", "Apt 4B", 12345, LocalDate.parse("2024-07-15"), "John Doe", readOrderDetailProjectionResponses,
// 			"Jane Smith", "098-765-4321", "123-456-7890", null, 0, "parkseol.dev@gmail.com");
//
// 		user = new User(504L, null, "parkseol",
// 			"01011111111", "parkseol", "parkseol.dev@gmail.com", "asdi2u34911!oj$@eI723",
// 			LocalDate.parse("2024-06-28"), LocalDateTime.now(), LocalDateTime.now(), UserStatus.ACTIVE, LocalDateTime.now(),
// 			true);
//
// 		pointLogResponse = new PointLogResponse(LocalDateTime.now(), "주문 시 포인트 적립", 30, 60446);
//
// 		readOrderStatusResponses.add(readOrderStatusResponse);
//
// 		readDeliveryPolicyResponse = new ReadDeliveryPolicyResponse(1, "기본", 20000, 0, false);
// 	}
//
// 	@Test
// 	@DisplayName("주문 리스트 조회 - 관리자")
// 	void getOrders_isAdmin() throws Exception {
// 		UserInfo testUserInfo = mock(UserInfo.class);
// 		when(testUserInfo.isAdmin()).thenReturn(true);
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
//
// 		Map<String, Object> data = new HashMap<>();
// 		data.put("responseData", List.of(readOrdersResponse));
// 		data.put("hasNext", true);
// 		when(orderService.readOrders(any(ReadOrdersRequest.class))).thenReturn(data);
//
// 		MockHttpServletRequest request = new MockHttpServletRequest();
// 		request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
//
// 		mockMvc.perform(post("/api/orders/list")
// 				.content(objectMapper.writeValueAsString(readOrdersRequest))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.requestAttr(AuthService.LOGIN_ID, "testLoginId"))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readOrders(any(ReadOrdersRequest.class));
// 	}
//
// 	@Test
// 	@DisplayName("주문 등록 - 비회원")
// 	void createOrder_nonMember() throws Exception {
// 		String loginId = null;
//
// 		when(orderService.createOrder(any())).thenReturn(readOrderResponse);
//
// 		mockMvc.perform(post("/api/orders/register")
// 				.content(objectMapper.writeValueAsString(createOrderRequest))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).createOrder(any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 등록 - 회원")
// 	void createOrder() throws Exception {
// 		String loginId = "testLoginId";
// 		UserInfo testUserInfo = mock(UserInfo.class);
//
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
// 		when(orderService.createOrder(any())).thenReturn(readOrderResponse);
//
// 		MockHttpServletRequest request = new MockHttpServletRequest();
// 		request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
//
// 		mockMvc.perform(post("/api/orders/register")
// 				.content(objectMapper.writeValueAsString(createOrderRequest))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.requestAttr(AuthService.LOGIN_ID, "testLoginId"))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).createOrder(any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상태 수정 - 관리자")
// 	void updateOrder_isAdmin() throws Exception {
// 		UserInfo testUserInfo = mock(UserInfo.class);
//
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
// 		when(testUserInfo.isAdmin()).thenReturn(true);
// 		when(orderService.updateOrderWithAdmin(any())).thenReturn(readOrderResponse);
//
// 		MockHttpServletRequest request = new MockHttpServletRequest();
// 		request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
//
// 		mockMvc.perform(put("/api/orders")
// 				.content(objectMapper.writeValueAsString(updateOrderRequest))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.requestAttr(AuthService.LOGIN_ID, "testLoginId"))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).updateOrderWithAdmin(any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 상태 수정- 관리자")
// 	void updateOrderDetail_isAdmin() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
// 		when(orderService.updateOrderDetailWithAdmin(any())).thenReturn(readOrderDetailResponse1);
//
// 		mockMvc.perform(put("/api/orders/detail")
// 				.content(objectMapper.writeValueAsString(updateOrderRequest))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.with(request -> {
// 					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
// 					return request;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).updateOrderDetailWithAdmin(any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 상태 수정")
// 	void updateOrderDetail() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo2);
// 		when(orderService.updateOrderDetail(any(), anyString())).thenReturn(readOrderDetailResponse1);
//
// 		mockMvc.perform(put("/api/orders/detail")
// 				.content(objectMapper.writeValueAsString(updateOrderRequest))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.with(request -> {
// 					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
// 					return request;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).updateOrderDetail(any(), anyString());
// 	}
//
// 	@Test
// 	@DisplayName("주문 조회")
// 	void getOrder() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo2);
// 		when(orderService.readOrder(any(), anyString())).thenReturn(readOrderResponse);
//
// 		mockMvc.perform(post("/api/orders/id")
// 				.content(objectMapper.writeValueAsString(readOrdersRequest))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.with(request -> {
// 					request.setAttribute(AuthService.LOGIN_ID, "testLoginId");
// 					return request;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readOrder(any(), anyString());
// 	}
//
// 	@Test
// 	@DisplayName("비회원 주문 조회")
// 	void getOrderWithoutLogin() throws Exception {
// 		when(orderService.readOrderWithoutLogin(any())).thenReturn(readOrderResponse);
//
// 		mockMvc.perform(post("/api/orders/non-member")
// 				.content(objectMapper.writeValueAsString(new ReadOrderWithoutLoginRequest()))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readOrderWithoutLogin(any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상태 이름으로 조회")
// 	void getOrderStatusByName() throws Exception {
// 		when(orderService.readOrderStatusByName(anyString())).thenReturn(new ReadOrderStatusResponse());
//
// 		mockMvc.perform(post("/api/orders/status/name")
// 				.content(objectMapper.writeValueAsString(new ReadOrderStatusByNameRequest(anyString())))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readOrderStatusByName(anyString());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상태 모두 조회")
// 	void getAllOrderStatus() throws Exception {
// 		when(orderService.readAllOrderStatus()).thenReturn(readOrderStatusResponses);
//
// 		mockMvc.perform(get("/api/orders/status/all")
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readAllOrderStatus();
// 	}
//
// 	@Test
// 	@DisplayName("운임비 정책 조회")
// 	void testGetDeliveryPolicy() throws Exception {
// 		ReadDeliveryPolicyResponse response = new ReadDeliveryPolicyResponse();
// 		when(orderService.readDeliveryPolicyById(anyInt())).thenReturn(response);
//
// 		ReadDeliveryPolicyRequest request = new ReadDeliveryPolicyRequest();
// 		mockMvc.perform(post("/api/orders/delivery-policy/id")
// 				.content(objectMapper.writeValueAsString(request))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readDeliveryPolicyById(anyInt());
// 	}
//
// 	@Test
// 	@DisplayName("운임비 정책 모두 조회")
// 	void testGetAllDeliveryPolicy() throws Exception {
// 		List<ReadDeliveryPolicyResponse> response = List.of(new ReadDeliveryPolicyResponse());
// 		when(orderService.readAllDeliveryPolicy()).thenReturn(response);
//
// 		mockMvc.perform(get("/api/orders/delivery-policy/all"))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readAllDeliveryPolicy();
// 	}
//
// 	@Test
// 	@DisplayName("운임비 정책 등록")
// 	void testCreateDeliveryPolicy() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
// 		CreateDeliveryPolicyRequest request = new CreateDeliveryPolicyRequest();
// 		ReadDeliveryPolicyResponse response = new ReadDeliveryPolicyResponse();
// 		when(orderService.createDeliveryPolicy(any(CreateDeliveryPolicyRequest.class))).thenReturn(response);
//
// 		mockMvc.perform(post("/api/orders/delivery-policy")
// 				.content(objectMapper.writeValueAsString(request))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.with(req -> {
// 					req.setAttribute(AuthService.LOGIN_ID, "parkseol");
// 					return req;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).createDeliveryPolicy(any());
// 	}
//
// 	@Test
// 	@DisplayName("운임비 정책 삭제")
// 	void testDeleteDeliveryPolicy() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
//
// 		mockMvc.perform(delete("/api/orders/delivery-policy/{id}", 1)
// 				.with(req -> {
// 					req.setAttribute(AuthService.LOGIN_ID, "testLoginId");
// 					return req;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).deleteDeliveryPolicy(anyInt());
// 	}
//
// 	@Test
// 	@DisplayName("포장 조회")
// 	void testGetWrapping() throws Exception {
// 		ReadWrappingResponse response = new ReadWrappingResponse();
// 		when(orderService.readWrappingById(anyInt())).thenReturn(response);
//
// 		ReadWrappingRequest request = new ReadWrappingRequest();
// 		mockMvc.perform(post("/api/orders/wrapping/id")
// 				.content(objectMapper.writeValueAsString(request))
// 				.contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readWrappingById(anyInt());
// 	}
//
// 	@Test
// 	@DisplayName("포장 모두 조회")
// 	void testGetAllWrappings() throws Exception {
// 		List<ReadWrappingResponse> response = List.of(new ReadWrappingResponse());
// 		when(orderService.readAllWrapping()).thenReturn(response);
//
// 		mockMvc.perform(get("/api/orders/wrapping/all"))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).readAllWrapping();
// 	}
//
// 	@Test
// 	@DisplayName("포장 등록")
// 	void testCreateWrapping() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
// 		CreateWrappingRequest request = new CreateWrappingRequest();
// 		ReadWrappingResponse response = new ReadWrappingResponse();
// 		when(orderService.createWrapping(any(CreateWrappingRequest.class))).thenReturn(response);
//
// 		mockMvc.perform(post("/api/orders/wrapping")
// 				.content(objectMapper.writeValueAsString(request))
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.with(req -> {
// 					req.setAttribute(AuthService.LOGIN_ID, "testLoginId");
// 					return req;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).createWrapping(any(CreateWrappingRequest.class));
// 	}
//
// 	@Test
// 	@DisplayName("포장 삭제")
// 	void testDeleteWrapping() throws Exception {
// 		when(userService.getUserInfoByLoginId(anyString())).thenReturn(testUserInfo);
//
// 		mockMvc.perform(delete("/api/orders/wrapping/{id}", 1)
// 				.with(req -> {
// 					req.setAttribute(AuthService.LOGIN_ID, "testLoginId");
// 					return req;
// 				}))
// 			.andExpect(status().isOk());
//
// 		verify(orderService).deleteWrapping(anyInt());
// 	}
// }
