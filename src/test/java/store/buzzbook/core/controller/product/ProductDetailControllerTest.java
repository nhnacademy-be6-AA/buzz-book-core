package store.buzzbook.core.controller.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.dto.product.ProductDetailResponse;
import store.buzzbook.core.dto.review.ReviewResponse;
import store.buzzbook.core.service.product.ProductDetailService;

@WebMvcTest(ProductDetailController.class)
class ProductDetailControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductDetailService productDetailService;

	@Mock
	BookResponse bookResponse;
	@Mock
	ReviewResponse reviewResponses1;
	@Mock
	ReviewResponse reviewResponses2;

	@Test
	@DisplayName("GET ProductDetail")
	void testGetProductDetail() throws Exception {

		int productId = 1;
		ProductDetailResponse mockResponse = ProductDetailResponse.builder()
			.book(bookResponse)
			.reviews(List.of(reviewResponses1, reviewResponses2))
			.build();

		Mockito.when(productDetailService.getProductDetail(productId))
			.thenReturn(mockResponse);

		mockMvc.perform(get("/api/products/{id}/detail", productId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
}
