package store.buzzbook.core.controller.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.buzzbook.core.dto.product.response.BookResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.service.product.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Management API", description = "도서 정보 CRUD API")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable int id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBookById(@PathVariable int id) {
//        bookService.deleteBookById(id);
//        return ResponseEntity.noContent().build();
//    }

}
