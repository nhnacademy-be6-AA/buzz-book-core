package store.buzzbook.core.service.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;

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

	@Test
	@DisplayName("search books")
	void searchBooks() {
		// given

		// when

		// then
	}

	@Test
	@DisplayName("search and save books")
	void searchAndSaveBooks(String query) {
		// given

		// when

		// then
	}

	@Test
	@DisplayName("save books to database")
	void saveBooksToDatabase() {
		// given

		// when

		// then
	}
}
