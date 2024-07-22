package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;

import store.buzzbook.core.dto.product.BookApiRequest;
import store.buzzbook.core.repository.product.AuthorRepository;
import store.buzzbook.core.repository.product.BookAuthorRepository;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.PublisherRepository;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private PublisherRepository publisherRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private BookAuthorRepository bookAuthorRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CharacterEncodingFilter characterEncodingFilter;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private BookSearchService bookSearchService;

	private BookApiRequest.Item testItem;
	private BookApiRequest response;

	@BeforeEach
	void setUp() {
		testItem = BookApiRequest.Item.builder()
			.title("test")
			.isbn("123456789")
			.publisher("test")
			.pubDate("2023-01-01")
			.category("test")
			.cover("test")
			.build();

		response = new BookApiRequest(List.of(testItem));
	}

	@Test
	@DisplayName("search books")
	void searchBooks() {
		// given
		String query = "testQuery";

		// when
		List<BookApiRequest.Item> result = bookSearchService.searchBooks(query);

		// then
		assertEquals(0, result.size());
	}

	@Test
	@DisplayName("search and save books")
	void searchAndSaveBooks() {
		// given
		String query = "testQuery";

		// when & then
		bookSearchService.searchAndSaveBooks(query);
	}

	@Test
	@DisplayName("save books to database")
	void saveBooksToDatabase() {
		// given
		when(bookRepository.existsByIsbn(anyString())).thenReturn(false);
		when(publisherRepository.findByName(anyString())).thenReturn(null);
		when(categoryRepository.findByName(anyString())).thenReturn(null);

		// when
		bookSearchService.saveBooksToDatabase(List.of(testItem));

		// then
		verify(bookRepository, times(1)).existsByIsbn(anyString());
		verify(bookRepository, times(1)).save(any());
		verify(publisherRepository, times(1)).save(any());
		verify(categoryRepository, times(1)).save(any());
	}
}
