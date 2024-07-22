package store.buzzbook.core.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.BookRequest;
import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.dto.product.ProductResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.Publisher;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.PublisherRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	private final String testTitle = "Test Title";
	private final String testDescription = "Test Description";
	private final String testISBN = "test123456789";
	private final String testPublisherName = "Test Publisher";
	private final String testPublishDateString = "2020-01-01";
	private final Integer productId = 1;
	private final long testBookId = 1;
	private final LocalDate testPublishDate = LocalDate.parse(testPublishDateString);
	@Mock
	Category category;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private PublisherRepository publisherRepository;
	@Mock
	private ProductRepository productRepository;
	@InjectMocks
	private BookService bookService;
	private BookRequest bookRequest;
	private Book book;
	@Mock
	private Publisher testPublisher;

	@BeforeEach
	void setUp() {
		bookRequest = BookRequest.builder()
			.title(testTitle)
			.description(testDescription)
			.isbn(testISBN)
			.publisher(testPublisherName)
			.publishDate(testPublishDateString)
			.productId(productId)
			.build();
	}

	@Test
	@DisplayName("save book")
	void testSaveBook() {
		Book newBook = new Book(testTitle, testDescription, testISBN, testPublisher, testPublishDateString);

		when(publisherRepository.findByName(testPublisherName)).thenReturn(testPublisher);
		when(bookRepository.save(any(Book.class))).thenReturn(newBook);
		when(testPublisher.getName()).thenReturn(testPublisherName);

		// When
		BookResponse response = bookService.saveBook(bookRequest);

		// Then

		verify(publisherRepository, times(0)).save(any(Publisher.class));
		verify(bookRepository, times(1)).save(any(Book.class));

		assertNotNull(response);
		assertEquals(response.getTitle(), testTitle);
		assertEquals(response.getDescription(), testDescription);
		assertEquals(response.getIsbn(), testISBN);
		assertEquals(response.getPublisher(), testPublisherName);
		assertEquals(response.getPublishDate(), testPublishDate);

	}

	@Test
	@DisplayName("save book - new publisher")
	void testSaveBookNewPublisher() {

		when(publisherRepository.findByName(testPublisherName)).thenReturn(null);
		when(publisherRepository.save(any(Publisher.class))).thenReturn(testPublisher);
		when(testPublisher.getName()).thenReturn(testPublisherName);

		// When
		BookResponse response = bookService.saveBook(bookRequest);

		// Then
		verify(publisherRepository, times(1)).save(any(Publisher.class));
		assertNotNull(response);
		assertEquals(response.getPublisher(), testPublisherName);
	}

	@Test
	@DisplayName("getAllBooks")
	void testGetAllBooks() {

		int pageNo = 0;
		int pageSize = 5;

		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<Book> testBookList = IntStream.rangeClosed(1, 10)
			.mapToObj(
				i -> new Book(testTitle + i, testDescription + i, testISBN + i, testPublisher, testPublishDateString))
			.toList();
		Page<Book> page = new PageImpl<>(testBookList, pageable, testBookList.size());

		when(bookRepository.findAll(pageable)).thenReturn(page);
		when(testPublisher.getName()).thenReturn(testPublisherName);

		// When
		Page<BookResponse> result = bookService.getAllBooks(pageNo, pageSize);

		// Then
		assertEquals(page.getTotalElements(), result.getTotalElements());
		assertEquals(page.getTotalPages(), result.getTotalPages());
		assertEquals(page.getNumberOfElements(), result.getNumberOfElements());

		List<BookResponse> responseContent = result.getContent();
		assertEquals(page.get().count(), responseContent.size());
		assertEquals(testBookList.getFirst().getTitle(), responseContent.getFirst().getTitle());
		assertEquals(testBookList.getLast().getTitle(), responseContent.getLast().getTitle());
		assertEquals(testBookList.getFirst().getIsbn(), responseContent.getFirst().getIsbn());
		assertEquals(testBookList.getLast().getIsbn(), responseContent.getLast().getIsbn());
	}

	@Test
	@DisplayName("getAllBooksExistProductId")
	void testGetAllBooksExistProductId() {
		int pageNo = 0;
		int pageSize = 5;

		Pageable pageable = PageRequest.of(pageNo, pageSize);

		List<Book> testBookList = IntStream.rangeClosed(1, 10)
			.mapToObj(
				i -> new Book(testTitle + i, testDescription + i, testISBN + i, testPublisher, testPublishDateString))
			.toList();
		Page<Book> page = new PageImpl<>(testBookList, pageable, testBookList.size());

		when(bookRepository.findAllByProductIdIsNotNull(pageable)).thenReturn(page);
		when(testPublisher.getName()).thenReturn(testPublisherName);

		// When
		Page<BookResponse> result = bookService.getAllBooksExistProductId(pageNo, pageSize);

		// Then
		assertEquals(page.getTotalElements(), result.getTotalElements());
		assertEquals(page.getTotalPages(), result.getTotalPages());
		assertEquals(page.getNumberOfElements(), result.getNumberOfElements());

		List<BookResponse> responseContent = result.getContent();
		assertEquals(page.get().count(), responseContent.size());
		assertEquals(testBookList.getFirst().getTitle(), responseContent.getFirst().getTitle());
		assertEquals(testBookList.getLast().getTitle(), responseContent.getLast().getTitle());
		assertEquals(testBookList.getFirst().getIsbn(), responseContent.getFirst().getIsbn());
		assertEquals(testBookList.getLast().getIsbn(), responseContent.getLast().getIsbn());
	}

	@Test
	@DisplayName("getBookById")
	void testGetBookById() {

		book = new Book(testTitle, testDescription, testISBN, testPublisher, testPublishDateString);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

		// When
		BookResponse result = bookService.getBookById(testBookId);

		// Then
		assertNotNull(result);
		assertEquals(book.getTitle(), result.getTitle());
		assertEquals(book.getDescription(), result.getDescription());
		assertEquals(book.getIsbn(), result.getIsbn());
		assertEquals(book.getPublishDate(), result.getPublishDate());

	}

	@Test
	@DisplayName("getBookByProductId")
	void testGetBookByProductId() {

		book = new Book(testTitle, testDescription, testISBN, testPublisher, testPublishDateString);
		Product product = Product.builder()
			.stock(20)
			.productName("Test Product")
			.description("Test Description")
			.price(999)
			.forwardDate(testPublishDate)
			.score(1)
			.thumbnailPath(null)
			.category(category)
			.stockStatus(Product.StockStatus.SALE)
			.build();

		book.setProduct(product);

		when(bookRepository.findByProductId(productId)).thenReturn(book);

		// When
		BookResponse result = bookService.getBookByProductId(productId);

		// Then
		assertNotNull(result);
		assertEquals(book.getTitle(), result.getTitle());
		assertEquals(ProductResponse.convertToProductResponse(book.getProduct()).getProductName(),
			Objects.requireNonNull(result.getProduct()).getProductName());
	}

	@Test
	@DisplayName("getBookByProductId - NotFoundBook")
	void testGetBookByProductIdNotFound() {

		when(bookRepository.findByProductId(anyInt())).thenReturn(null);

		assertThrows(DataNotFoundException.class, () -> bookService.getBookByProductId(productId));
	}

	@Test
	@DisplayName("deleteBookById")
	void testDeleteBookById() {

		Book delTestBook = new Book(
			testTitle, testDescription, testISBN, testPublisher, testPublishDateString
		);

		Product product = Product.builder()
			.stock(20)
			.productName("Test Product")
			.description("Test Description")
			.price(999)
			.forwardDate(testPublishDate)
			.score(1)
			.thumbnailPath(null)
			.category(category)
			.stockStatus(Product.StockStatus.SALE)
			.build();

		delTestBook.setProduct(product);

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(delTestBook));
		when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));

		BookResponse result = bookService.deleteBookById(delTestBook.getId());
		assertNotNull(result);
		assertNull(result.getProduct());
		assertEquals(delTestBook.getTitle(), result.getTitle());

		verify(productRepository, times(1)).findById(product.getId());
		verify(productRepository, times(1)).save(any(Product.class));
		verify(bookRepository, times(1)).save(any(Book.class));
	}

	@Test
	@DisplayName("deleteBookById - not found book")
	void testDeleteBookByIdNotFound() {

		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(DataNotFoundException.class, () -> bookService.getBookById(testBookId));
	}
}
