package store.buzzbook.core.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.buzzbook.core.dto.product.response.BookResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.repository.product.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    public BookResponse getBookById(int id) {
        Book book = bookRepository.findById(id).orElse(null);
        BookResponse bookResponse = BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .publishDate(book.getPublishDate())
                .build();
        return bookResponse;
    }

    //Todo order 응답할때 product response필요하니 만들어두기

    @Transactional
    public BookResponse updateBook(int id, String newTitle, String newDescription) {
        Book book = bookRepository.findById(id).orElse(null);

        if (book != null) {
            Book updateBookEntity = Book.builder()
                    .title(newTitle)
                    .description(newDescription)
                    .isbn(book.getIsbn())
                    .publisher(book.getPublisher())
                    .publishDate(String.valueOf(book.getPublishDate()))
                    .build();

            bookRepository.save(updateBookEntity);
            return BookResponse.builder()
                    .id(updateBookEntity.getId())
                    .title(updateBookEntity.getTitle())
                    .description(updateBookEntity.getDescription())
                    .isbn(updateBookEntity.getIsbn())
                    .publisher(updateBookEntity.getPublisher())
                    .publishDate(updateBookEntity.getPublishDate())
                    .build();
        }
        return null;
    }
}
