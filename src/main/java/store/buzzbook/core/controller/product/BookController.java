package store.buzzbook.core.controller.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.response.BookRequest;
import store.buzzbook.core.dto.product.response.BookResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.service.product.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Management API", description = "도서 정보 CRUD API")
public class BookController {

	private final BookService bookService;

	@GetMapping("/{id}")
	@Operation(summary = "id로 단일 책 조회", description = "PathVariable long id로 단일 책 조회")
	public ResponseEntity<BookResponse> getBookById(@PathVariable long id) {
		BookResponse book;
		try {
			book = bookService.getBookById(id);
		} catch (DataNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return ResponseEntity.ok(book);
	}

	@PostMapping
	@Operation(summary = "책 추가", description = "필요 DTO : BookRequest")
	public ResponseEntity<Book> createBook(@RequestBody BookRequest bookReq) {
		Book savedBook = bookService.saveBook(bookReq);
		return ResponseEntity.ok(savedBook);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "id로 책 삭제", description = "PathVariable long id로 도서 논리 삭제(product.product_status=SOLD_OUT, product.stock=0, book.product_id=null)")
	public ResponseEntity<BookResponse> deleteBookById(@PathVariable long id) {
		BookResponse bookResponse;
		try {
			bookResponse = bookService.deleteBookById(id);
		}catch (DataNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(bookResponse);
	}


	@GetMapping
	@Operation(summary = "조건으로 책 목록 조회", description = """
		다양한 조건에 따라 책 목록을 조회합니다.

		Integer productId=productId : productId로 책 단일 조회

		boolean hasProduct=true : 상품으로 등록된 책들만 조회
		"""
	)
	public ResponseEntity<Object> getBooks(
		// @RequestBody(required = false) List<Integer> productIdList,
		@RequestParam(required = false) Integer productId,
		@RequestParam(required = false, defaultValue = "false") boolean hasProduct
	) {

			if (productId != null) {
			// Case 2: RequestBody int productId 값이 있으면
			BookResponse book = bookService.getBookByProductId(productId);
			return ResponseEntity.ok(book);
		} else {
			// Case 3: RequestBody가 없고, boolean hasProduct 값에 따라 처리
			if (hasProduct) {
				return ResponseEntity.ok(bookService.getAllBooksExistProductId());
			} else {
				return ResponseEntity.ok(bookService.getAllBooks());
			}
		}
	}
}
