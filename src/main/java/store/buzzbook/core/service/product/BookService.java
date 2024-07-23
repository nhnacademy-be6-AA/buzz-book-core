package store.buzzbook.core.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.BookRequest;
import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.Publisher;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.PublisherRepository;

@RequiredArgsConstructor
@Service
public class BookService {

	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;
	private final ProductRepository productRepository;

	public BookResponse saveBook(BookRequest bookReq) {
		Publisher publisher = publisherRepository.findByName(bookReq.getPublisher());

		if (publisher == null) {
			publisher = publisherRepository.save(new Publisher(bookReq.getPublisher()));
		}

		Book newBook = Book.builder()
			.title(bookReq.getTitle())
			.description(bookReq.getDescription())
			.isbn(bookReq.getIsbn())
			.publisher(publisher)
			.publishDate(bookReq.getPublishDate())
			.build();

		bookRepository.save(newBook);

		return BookResponse.convertToBookResponse(newBook);
	}

	public Page<BookResponse> getAllBooks(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return bookRepository.findAll(pageable).map(BookResponse::convertToBookResponse);
	}

	public Page<BookResponse> getAllBooksExistProductId(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return bookRepository.findAllByProductIdIsNotNull(pageable)
			.map(BookResponse::convertToBookResponse);
	}

	public BookResponse getBookById(long id) {
		Book book = bookRepository.findById(id).orElse(null);
		if (book == null) {
			throw new DataNotFoundException("book", id);
		}
		return BookResponse.convertToBookResponse(book);
	}

	public BookResponse getBookByProductId(int productId) {
		Book book = bookRepository.findByProductId(productId);
		if (book == null) {
			throw new DataNotFoundException("book.productId", productId);
		}
		return BookResponse.convertToBookResponse(book);
	}

	public BookResponse deleteBookById(long id) {

		Book book = bookRepository.findById(id).orElse(null);
		if (book == null) {
			throw new DataNotFoundException("book", id);
		}

		Product product = productRepository.findById(book.getProduct().getId()).orElseThrow();
		Product newProduct = new Product(product.getId(), 0, product.getProductName(), product.getDescription(), product.getPrice(),
			product.getForwardDate(), product.getScore(), product.getThumbnailPath(), Product.StockStatus.SOLD_OUT,
			product.getCategory(), product.getProductTags());
		productRepository.save(newProduct);
		book.setProduct(null);
		bookRepository.save(book);
		return BookResponse.convertToBookResponse(book);
	}
}
