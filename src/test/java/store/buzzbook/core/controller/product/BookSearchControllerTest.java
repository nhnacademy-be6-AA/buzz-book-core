package store.buzzbook.core.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.buzzbook.core.service.product.BookSearchService;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookSearchController.class)
public class BookSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookSearchService bookSearchService;

	@Autowired
	private ObjectMapper objectMapper;

	// @Test
	// @DisplayName("Aladin API에서 도서 검색 테스트")
	// void testSearchBooks() throws Exception {
	// 	// given
	// 	String query = "testQuery";
	// 	BookApiRequest.Item item = BookApiRequest.Item.builder()
	// 		.title("testBook")
	// 		.description("A description of testBook")
	// 		.isbn("1234567890")
	// 		.author("Author Name")
	// 		.publisher("Publisher Name")
	// 		.pubDate("2024-07-01")
	// 		.cover("http://example.com/cover.jpg")
	// 		.customerReviewRank(5)
	// 		.category("Category Name")
	// 		.priceStandard(10000)
	// 		.priceSales(9000)
	// 		.stock("10")
	// 		.product("Product Name")
	// 		.build();
	//
	// 	List<BookApiRequest.Item> items = List.of(item);
	//
	// 	when(bookSearchService.searchBooks(anyString())).thenReturn(items);
	//
	// 	// when & then
	// 	mockMvc.perform(get("/api/books/search")
	// 			.param("query", query)
	// 			.contentType(MediaType.APPLICATION_JSON))
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$[0].title").value("testBook"))
	// 		.andExpect(jsonPath("$[0].description").value("A description of testBook"))
	// 		.andExpect(jsonPath("$[0].isbn").value("1234567890"))
	// 		.andExpect(jsonPath("$[0].author").value("Author Name"))
	// 		.andExpect(jsonPath("$[0].publisher").value("Publisher Name"))
	// 		.andExpect(jsonPath("$[0].pubDate").value("2024-07-01"))
	// 		.andExpect(jsonPath("$[0].cover").value("http://example.com/cover.jpg"))
	// 		.andExpect(jsonPath("$[0].customerReviewRank").value(5))
	// 		.andExpect(jsonPath("$[0].category").value("Category Name"))
	// 		.andExpect(jsonPath("$[0].priceStandard").value(10000))
	// 		.andExpect(jsonPath("$[0].priceSales").value(9000))
	// 		.andExpect(jsonPath("$[0].stock").value("10"))
	// 		.andExpect(jsonPath("$[0].product").value("Product Name"));
	//
	// 	verify(bookSearchService, times(1)).searchBooks(query);
	// }

	@Test
	@DisplayName("Aladin API에서 도서 검색 후 데이터베이스에 저장 테스트")
	void testSearchAndSaveBooks() throws Exception {
		// given
		String query = "testQuery";

		doNothing().when(bookSearchService).searchAndSaveBooks(query);

		// when & then
		mockMvc.perform(post("/api/books/search")
				.param("query", query)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(bookSearchService, times(1)).searchAndSaveBooks(query);
	}
}