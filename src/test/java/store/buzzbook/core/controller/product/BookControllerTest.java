package store.buzzbook.core.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import store.buzzbook.core.dto.product.BookRequest;
import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.service.product.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("도서 조회 테스트")
	void testGetBookById() throws Exception {
		// given
		long bookId = 1L;
		BookResponse bookResponse = BookResponse.builder()
			.id(bookId)
			.title("testBook")
			.build();

		when(bookService.getBookById(bookId)).thenReturn(bookResponse);

		// when & then
		mockMvc.perform(get("/api/books/{id}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(bookId))
			.andExpect(jsonPath("$.title").value("testBook"));

		verify(bookService, times(1)).getBookById(bookId);
	}

	@Test
	@DisplayName("도서 추가 테스트")
	void testCreateBook() throws Exception {
		// given
		BookRequest bookRequest = BookRequest.builder()
			.title("testBook")
			.build();
		BookResponse bookResponse = BookResponse.builder()
			.title("testBook")
			.build();

		when(bookService.saveBook(any(BookRequest.class))).thenReturn(bookResponse);

		// when & then
		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bookRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("testBook"));

		verify(bookService, times(1)).saveBook(any(BookRequest.class));
	}

	@Test
	@DisplayName("도서 삭제 테스트")
	void testDeleteBookById() throws Exception {
		// given
		long bookId = 1L;
		BookResponse bookResponse = BookResponse.builder()
			.id(bookId)
			.title("testBook")
			.build();

		when(bookService.deleteBookById(bookId)).thenReturn(bookResponse);

		// when & then
		mockMvc.perform(delete("/api/books/{id}", bookId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(bookId))
			.andExpect(jsonPath("$.title").value("testBook"));

		verify(bookService, times(1)).deleteBookById(bookId);
	}

	@Test
	@DisplayName("조건으로 책 목록 조회 테스트 - 상품 ID로 조회")
	void testGetBooksByProductId() throws Exception {
		// given
		int productId = 1;
		BookResponse bookResponse = BookResponse.builder()
			.title("testBook")
			.build();

		when(bookService.getBookByProductId(productId)).thenReturn(bookResponse);

		// when & then
		mockMvc.perform(get("/api/books")
				.param("productId", String.valueOf(productId))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("testBook"));

		verify(bookService, times(1)).getBookByProductId(productId);
	}

	@Test
	@DisplayName("조건으로 책 목록 조회 테스트 - 상품이 있는 책들 조회")
	void testGetBooksWithProducts() throws Exception {
		// given
		BookResponse bookResponse = BookResponse.builder()
			.title("testBook")
			.build();
		Page<BookResponse> bookResponses = new PageImpl<>(List.of(bookResponse));

		when(bookService.getAllBooksExistProductId(anyInt(), anyInt())).thenReturn(bookResponses);

		// when & then
		mockMvc.perform(get("/api/books")
				.param("hasProduct", "true")
				.param("pageNo", "0")
				.param("pageSize", "10")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].title").value("testBook"));

		verify(bookService, times(1)).getAllBooksExistProductId(anyInt(), anyInt());
	}

	@Test
	@DisplayName("조건으로 책 목록 조회 테스트 - 모든 책 조회")
	void testGetAllBooks() throws Exception {
		// given
		BookResponse bookResponse = BookResponse.builder()
			.title("testBook")
			.build();
		Page<BookResponse> bookResponses = new PageImpl<>(List.of(bookResponse));

		when(bookService.getAllBooks(anyInt(), anyInt())).thenReturn(bookResponses);

		// when & then
		mockMvc.perform(get("/api/books")
				.param("pageNo", "0")
				.param("pageSize", "10")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].title").value("testBook"));

		verify(bookService, times(1)).getAllBooks(anyInt(), anyInt());
	}
}