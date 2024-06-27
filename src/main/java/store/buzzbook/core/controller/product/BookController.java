package store.buzzbook.core.controller.product;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import store.buzzbook.core.dto.product.BookRequest;
import store.buzzbook.core.dto.product.BookResponse;
import store.buzzbook.core.service.product.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "도서 관리", description = "도서 CR_D")
public class BookController {

	private final BookService bookService;


	@GetMapping("/{id}")
	@Operation(summary = "도서 조회", description = "주어진 id(long)에 해당하는 도서 정보 조회")
	@ApiResponse(responseCode = "200", description = "도서 조회가 성공시 도서의 BookResponse 반환")

	public ResponseEntity<BookResponse> getBookById(@PathVariable long id) {
		BookResponse book = bookService.getBookById(id);
		return ResponseEntity.ok(book);
	}


	@PostMapping
	@Operation(summary = "도서 추가", description = "새로운 도서를 추가.<br>요청 본문에는 BookRequest DTO 사용.")
	@ApiResponse(responseCode = "200", description = "도서 추가 성공시 추가된 도서의 BookResponse 반환")

	public ResponseEntity<BookResponse> createBook(
		@RequestBody @Parameter(description = "추가할 도서의 정보(BookRequest)", required = true) BookRequest bookReq) {
		BookResponse savedBook = bookService.saveBook(bookReq);
		return ResponseEntity.ok(savedBook);
	}


	@DeleteMapping("/{id}")
	@Operation(summary = "도서 삭제", description = "주어진 id(long)에 해당하는 도서 삭제.<br>데이터가 물리적으로 삭제되지는 않음.<br>(product.product_status=SOLD_OUT, product.stock=0, book.product_id=null)")
	@ApiResponse(responseCode = "200", description = "도서 삭제 성공시 삭제된 도서의 BookResponse 반환")

	public ResponseEntity<BookResponse> deleteBookById(@PathVariable long id) {
		return ResponseEntity.ok(bookService.deleteBookById(id));
	}


	@GetMapping
	@Operation(summary = "조건으로 책 목록 조회", description = "책을 id 순서로 조회")
	@ApiResponse(responseCode = "200", description = "조회 성공시 page<bookResponse> 반환<br>상품 id(int) 값이 존재하고 조회 성공시 bookResponse 반환")

	public ResponseEntity<?> getBooks(
		@RequestParam(required = false, defaultValue = "0") @Parameter(description = "페이지 번호")
		Integer pageNo,
		@RequestParam(required = false, defaultValue = "10") @Parameter(description = "한 페이지에 보여질 아이템 수")
		Integer pageSize,
		@RequestParam(required = false, defaultValue = "false") @Parameter(description = "상품으로 등록된 책들만 조회할지 여부")
		Boolean hasProduct,
		@RequestParam(required = false) @Parameter(description = "상품 id(int)로 책을 조회할 경우 사용")
		Integer productId) {

		if (productId != null) {
			BookResponse book = bookService.getBookByProductId(productId);
			return ResponseEntity.ok(book);
		} else if (Boolean.TRUE.equals(hasProduct)) {
			return ResponseEntity.ok(bookService.getAllBooksExistProductId(pageNo, pageSize));
		}
		return ResponseEntity.ok(bookService.getAllBooks(pageNo, pageSize));
	}
}
