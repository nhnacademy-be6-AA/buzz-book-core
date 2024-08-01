package store.buzzbook.core.controller.product;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.dto.product.ProductRequest;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.dto.product.ProductUpdateRequest;
import store.buzzbook.core.service.product.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService productService;

	@Test
	@DisplayName("상품 추가 테스트")
	void testCreateProduct() throws Exception {
		// given
		ProductRequest productRequest = ProductRequest.builder()
			.productName("testProduct")
			.build();
		ProductResponse productResponse = ProductResponse.builder()
			.productName("testProduct")
			.build();

		when(productService.saveProduct(any(ProductRequest.class))).thenReturn(productResponse);

		// when & then
		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(productRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.productName").value(productResponse.getProductName()));

		verify(productService, times(1)).saveProduct(any(ProductRequest.class));
	}

	// @Test
	// @DisplayName("조건으로 상품 목록 조회 테스트")
	// void testGetAllProduct() throws Exception {
	// 	// given
	// 	ProductResponse productResponse = ProductResponse.builder()
	// 		.productName("testProduct")
	// 		.build();
	// 	Page<ProductResponse> productResponses = new PageImpl<>(Collections.singletonList(productResponse));
	//
	// 	when(productService.getProductsByCriteria(any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(productResponses);
	//
	// 	// when & then
	// 	mockMvc.perform(get("/api/products")
	// 			.param("status", "SALE")
	// 			.param("name", "test")
	// 			.param("categoryId", "1")
	// 			.param("orderBy", "name")
	// 			.param("pageNo", "0")
	// 			.param("pageSize", "10")
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.content[0].productName").value(productResponse.getProductName()));
	//
	// 	verify(productService, times(1)).getProductsByCriteria(any(), any(), any(), any(), anyInt(), anyInt());
	// }

	@Test
	@DisplayName("ID로 상품 조회 테스트")
	void testGetProductById() throws Exception {
		// given
		int productId = 1;
		ProductResponse productResponse = ProductResponse.builder()
			.productName("testProduct")
			.build();

		when(productService.getProductById(productId)).thenReturn(productResponse);

		// when & then
		mockMvc.perform(get("/api/products/{id}", productId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.productName").value(productResponse.getProductName()));

		verify(productService, times(1)).getProductById(productId);
	}

	@Test
	@DisplayName("상품 수정 테스트")
	void testUpdateProduct() throws Exception {
		// given
		int productId = 1;
		ProductUpdateRequest productUpdateRequest = ProductUpdateRequest.builder()
			.productName("updatedProduct")
			.build();
		ProductResponse productResponse = ProductResponse.builder()
			.productName("updatedProduct")
			.build();

		when(productService.updateProduct(anyInt(), any(ProductUpdateRequest.class))).thenReturn(productResponse);

		// when & then
		mockMvc.perform(put("/api/products/{id}", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(productUpdateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.productName").value(productResponse.getProductName()));

		verify(productService, times(1)).updateProduct(anyInt(), any(ProductUpdateRequest.class));
	}

	// 추가 엔드포인트 테스트

	@Test
	@DisplayName("상품 제목으로 검색 테스트")
	void testGetAllProductsByTitle() throws Exception {
		// given
		String title = "testTitle";
		ProductResponse productResponse = ProductResponse.builder()
			.productName("testProduct")
			.build();
		List<ProductResponse> productResponses = Collections.singletonList(productResponse);

		when(productService.getAllProductsByTitle(title)).thenReturn(productResponses);

		// when & then
		mockMvc.perform(get("/api/products/search")
				.param("title", title)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productName").value(productResponse.getProductName()));

		verify(productService, times(1)).getAllProductsByTitle(title);
	}

	@Test
	@DisplayName("최신 상품 조회 테스트")
	void testGetLatestProducts() throws Exception {
		// given
		int count = 5;
		ProductResponse productResponse = ProductResponse.builder()
			.productName("latestProduct")
			.build();
		List<ProductResponse> productResponses = Collections.singletonList(productResponse);

		when(productService.getLatestProducts(count)).thenReturn(productResponses);

		// when & then
		mockMvc.perform(get("/api/products/latest")
				.param("count", String.valueOf(count))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productName").value(productResponse.getProductName()));

		verify(productService, times(1)).getLatestProducts(count);
	}

	// Helper method to convert an object to JSON string
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}