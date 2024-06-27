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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.common.exception.product.DataNotFoundException;
import store.buzzbook.core.dto.product.BookRequest;
import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.service.product.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Management API", description = "도서 정보 CRUD API")
public class BookController {

	private final BookService bookService;

	@GetMapping("/{id}")
	@Operation(summary = "도서 조회", description = "주어진 id(long)에 해당하는 도서 정보를 조회합니다.")
	@ApiResponses(value =
		{@ApiResponse(responseCode = "200", description = "성공적으로 도서 정보를 조회한 경우"),
		@ApiResponse(responseCode = "404", description = "주어진 ID에 해당하는 도서가 없는 경우")})

	public ResponseEntity<BookResponse> getBookById(
		@PathVariable @Parameter(description = "조회할 도서의 ID", required = true) long id) {
		BookResponse book = bookService.getBookById(id);
		return ResponseEntity.ok(book);
	}

	@PostMapping
	@Operation(summary = "도서 추가", description = "새로운 도서를 추가합니다. 요청 본문에는 BookRequest DTO를 사용합니다.")
	@ApiResponses(value =
		{@ApiResponse(responseCode = "200", description = "도서 추가가 성공하고 추가된 도서 정보를 반환한 경우"),
		@ApiResponse(responseCode = "400", description = "요청 본문의 데이터가 유효하지 않은 경우")})

	public ResponseEntity<Book> createBook(
		@RequestBody @Parameter(description = "추가할 도서의 정보", required = true) BookRequest bookReq) {
		Book savedBook = bookService.saveBook(bookReq);
		return ResponseEntity.ok(savedBook);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "도서 삭제", description = "PathVariable long id로 도서 논리 삭제(product.product_status=SOLD_OUT, product.stock=0, book.product_id=null)")
	public ResponseEntity<BookResponse> deleteBookById(@PathVariable long id) {
		BookResponse bookResponse;
		try {
			bookResponse = bookService.deleteBookById(id);
		} catch (DataNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(bookResponse);
	}

	@GetMapping
	@Operation(summary = "조건으로 책 목록 조회", description = """
		조건이 없을경우 모든 책 조회후 page<bookResponse>로 반환
		boolean hasProduct=true : 상품으로 등록된 책들만 조회
		int productId = {product_id} : product_id로 단일 책 조회 후 bookResponse 반환
		""")

	public ResponseEntity<?> getBooks(@RequestParam(required = false, defaultValue = "0") Integer pageNo,
		@RequestParam(required = false, defaultValue = "10") Integer pageSize,
		@RequestParam(required = false, defaultValue = "false") Boolean hasProduct,
		@RequestParam(required = false) Integer productId) {

		if (productId != null) {
			BookResponse book = bookService.getBookByProductId(productId);
			return ResponseEntity.ok(book);
		} else if (Boolean.TRUE.equals(hasProduct)) {
			return ResponseEntity.ok(bookService.getAllBooksExistProductId(pageNo, pageSize));
		}
		return ResponseEntity.ok(bookService.getAllBooks(pageNo, pageSize));
	}
}
