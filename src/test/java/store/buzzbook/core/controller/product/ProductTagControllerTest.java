package store.buzzbook.core.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.buzzbook.core.service.product.ProductTagService;

import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductTagController.class)
public class ProductTagControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductTagService productTagService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("특정 상품에 대한 태그 리스트 조회 테스트")
	void testGetTagsByProductId() throws Exception {
		// given
		int productId = 1;
		List<String> tags = List.of("tag1", "tag2");

		when(productTagService.getTagsByProductId(productId)).thenReturn(tags);

		// when & then
		mockMvc.perform(get("/api/productTags/{productId}", productId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0]").value("tag1"))
			.andExpect(jsonPath("$[1]").value("tag2"));

		verify(productTagService, times(1)).getTagsByProductId(productId);
	}

	@Test
	@DisplayName("특정 상품에 대한 태그 추가 테스트")
	void testAddTagToProduct() throws Exception {
		// given
		int productId = 1;
		List<Integer> tagIds = List.of(1, 2);

		doNothing().when(productTagService).addTagToProduct(productId, tagIds);

		// when & then
		mockMvc.perform(post("/api/productTags/{productId}/tags", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagIds)))
			.andExpect(status().isOk());

		verify(productTagService, times(1)).addTagToProduct(productId, tagIds);
	}

	@Test
	@DisplayName("특정 상품에 대한 태그 삭제 테스트")
	void testRemoveTagFromProduct() throws Exception {
		// given
		int productId = 1;
		List<Integer> tagIds = List.of(1, 2);

		doNothing().when(productTagService).removeAllTagsFromProduct(productId, tagIds);

		// when & then
		mockMvc.perform(delete("/api/productTags/{productId}/tags", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagIds)))
			.andExpect(status().isNoContent());

		verify(productTagService, times(1)).removeAllTagsFromProduct(productId, tagIds);
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}