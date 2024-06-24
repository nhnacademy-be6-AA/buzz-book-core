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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
	@Operation(summary = "id로 단일 책 조회", description = "PathVariable long book_id로 단일 책 조회")
	public ResponseEntity<BookResponse> getBookById(@PathVariable long id) {
		BookResponse book = bookService.getBookById(id);
		return ResponseEntity.ok(book);
	}

	@PostMapping
	@Operation(summary = "책 추가", description = "관리자가 임의의 책을 DB에 등록.")
	public ResponseEntity<Book> createBook(@RequestBody BookRequest bookReq) {
		Book savedBook = bookService.saveBook(bookReq);
		return ResponseEntity.ok(savedBook);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "id로 책 삭제", description = "PathVariable long id로 도서 논리 삭제(product.product_status=SOLD_OUT, product.stock=0, book.product_id=null")
	public ResponseEntity<BookResponse> deleteBookById(@PathVariable long id) {
		BookResponse bookResponse = bookService.deleteBookById(id);
		return ResponseEntity.ok(bookResponse);
	}


	// @GetMapping
	// @Operation(summary = "모든 책 조회", description = "RequestParam (hasProduct==true)면 상품번호가 등록된 책들만 조회, param이 없거나 false면 모든 책 조회")
	// public ResponseEntity<List<BookResponse>> getAllBooks(@RequestParam boolean hasProduct) {
	// 	if (hasProduct) {
	// 		return ResponseEntity.ok(bookService.getAllBooksExistProductId());
	// 	} else {
	// 		return ResponseEntity.ok(bookService.getAllBooks());
	// 	}
	// }
	//
	// @GetMapping("/product")
	// @Operation(summary = "product_id List로 다수의 책 조회", description = "RequestBody List<Integer> product_id List로 다수의 책 조회")
	// public ResponseEntity<List<BookResponse>> getAllBooksByProductIdList(@RequestBody List<Integer> productIdList) {
	// 	List<BookResponse> bookList = bookService.getBooksByProductIdList(productIdList);
	// 	return ResponseEntity.ok(bookList);
	// }
	//
	// @GetMapping("/product/{productId}")
	// @Operation(summary = "product_id로 단일 책 조회", description = "RequestBody int product_id로 단일 책 조회")
	// public ResponseEntity<BookResponse> getBookByProductId(@PathVariable int productId) {
	// 	BookResponse book = bookService.getBookByProductId(productId);
	// 	return ResponseEntity.ok(book);
	// }

	@GetMapping
	@Operation(summary = "책 목록 조회", description = "다양한 조건에 따라 책 목록을 조회합니다.")
	public ResponseEntity<Object> getBooks(
		// @RequestBody(required = false) List<Integer> productIdList,
		@RequestParam(required = false) Integer productId,
		@RequestParam(required = false, defaultValue = "false") boolean hasProduct
	) {
		// if (productIdList != null && !productIdList.isEmpty()) {
		// 	// Case 1: RequestBody List<Integer> productIdList 값이 있으면
		// 	List<BookResponse> bookList = bookService.getBooksByProductIdList(productIdList);
		// 	return ResponseEntity.ok(bookList);
		// } else

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
