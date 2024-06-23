// package store.buzzbook.core.controller.order;
//
// import java.time.ZonedDateTime;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.BDDMockito;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import store.buzzbook.core.dto.order.ReadDeliveryPolicyResponse;
// import store.buzzbook.core.dto.order.ReadOrderDetailResponse;
// import store.buzzbook.core.dto.order.ReadOrderResponse;
// import store.buzzbook.core.dto.order.ReadOrderStatusResponse;
// import store.buzzbook.core.dto.order.ReadWrappingResponse;
// import store.buzzbook.core.dto.user.UserInfo;
// import store.buzzbook.core.entity.product.Category;
// import store.buzzbook.core.entity.product.Product;
// import store.buzzbook.core.entity.user.Grade;
// import store.buzzbook.core.entity.user.GradeName;
// import store.buzzbook.core.service.order.OrderService;
//
// @ExtendWith(MockitoExtension.class)
// public class OrderControllerTest {
//
// 	@Mock
// 	private OrderService orderService;
//
// 	@InjectMocks
// 	private OrderController orderController;
//
// 	private MockMvc mockMvc;
//
// 	private ObjectMapper objectMapper = new ObjectMapper();
//
// 	@BeforeEach
// 	void setUp() {
// 		mockMvc = MockMvcBuilders
// 			.standaloneSetup(orderController)
// 			.build();
// 	}
//
// 	@Test
// 	@DisplayName("주문 리스트 조회 - 관리자")
// 	void getOrdersTest() throws Exception {
// 		List<ReadOrderResponse> resultList = new ArrayList<>();
// 		PageRequest pageable = PageRequest.of(0, 1);
//
// 		List<ReadOrderDetailResponse> details = new ArrayList<>();
// 		details.add(new ReadOrderDetailResponse(1, 1000, 1, true, ZonedDateTime.now(), new ReadOrderStatusResponse(1, "환불", ZonedDateTime.now()),
// 			new ReadWrappingResponse(1, "신문지", 1000),
// 			Product.builder().productName("리눅스 구조").stock(1).stockStatus(Product.StockStatus.SALE).score(5).category(
// 					Category.builder().name("국내도서").id(1).build())
// 				.forwardDate(ZonedDateTime.now()).price(1000).thumbnailPath("aladin").build()));
//
// 		resultList.add(new ReadOrderResponse(1, 1000, "Thanks", "광주", "동구", 61459, ZonedDateTime.now(),
// 			"ps", new ReadDeliveryPolicyResponse(1, "정책1", 1000, 1000),
// 			details, new UserInfo("ps", "01012345678", "ps", "ps@gmail.com",
// 			ZonedDateTime.now(), Grade.builder().id(1).benefit(10000).name(GradeName.GOLD).standard(100).build(), true)));
//
// 		BDDMockito.given(orderService.readOrders(pageable)).willReturn(new PageImpl<>(resultList, pageable, 1));
//
// 		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
// 		params.add("login-id", "ps");
// 		params.add("is-admin", Boolean.TRUE.toString());
//
// 		mockMvc.perform(MockMvcRequestBuilders.get("/api/orders").queryParams(params).accept(MediaType.APPLICATION_JSON))
// 				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(resultList)));
//
// 		BDDMockito.verify(orderService).readOrders(pageable);
// 	}
// }
