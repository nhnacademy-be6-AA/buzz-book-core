package store.buzzbook.core.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.buzzbook.core.dto.product.BookApiRequest;
import store.buzzbook.core.service.product.BookSearchService;

import java.util.List;

// 외부에서 API 책 정보를 검색하고 DB에 저장하는 클래스
@RestController
@RequestMapping("/api/books/search")
@RequiredArgsConstructor

@Tag(name = "알라딘 API", description = "알라딘에서 도서 검색, DB에 등록")
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping
    @Operation(summary = "Search books from Aladin API", description = "aladin API에서 도서를 검색함")
    public List<BookApiRequest.Item> searchBooks(@RequestParam(required = false, defaultValue = "") String query) {
        return bookSearchService.searchBooks(query);
    }

    @PostMapping
    @Operation(summary = "Search and save books", description = "aladin API에서 도서를 검색하고 데이터베이스에 저장함")
    public void searchAndSaveBooks(@RequestParam(required = false, defaultValue = "") String query) {
        bookSearchService.searchAndSaveBooks(query);
    }
}
