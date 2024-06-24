package store.buzzbook.core.service.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.buzzbook.core.dto.product.response.BookResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.repository.product.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;

	public Book saveBook(Book book) {
		return bookRepository.save(book);
	}

	public List<BookResponse> getAllBooks() {
		return bookRepository.findAll().stream()
			.map(BookResponse::convertToBookResponse)
			.collect(Collectors.toList());
	}

	public BookResponse getBookById(long id) {
		Book book = bookRepository.findById(id).orElse(null);
		BookResponse bookResponse = BookResponse.builder()
			.id(book.getId())
			.title(book.getTitle())
			.description(book.getDescription())
			.isbn(book.getIsbn())
			.publisher(String.valueOf(book.getPublisher()))
			.publishDate(book.getPublishDate())
			.build();
		return bookResponse;
	}

	public void deleteBookById(long id) {
		bookRepository.deleteById(id);
	}

}
