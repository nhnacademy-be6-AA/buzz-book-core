package store.buzzbook.core.elastic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.service.product.BookSearchService;

@Service
@RequiredArgsConstructor
@Transactional
public class DataTransferService {

	private final BookRepository bookRepository;
	private final BookSearchService bookSearchService;

	// MySQL에서 Elasticsearch로 데이터 동기화
	public Long mySqlDataTransferToElastic() {
		List<Book> books = bookRepository.findAll();

		for (Book book : books) {
				bookSearchService.indexBookToElasticsearch(book);
		}
		return (long) books.size(); // 총 전송된 데이터 갯수 반환
	}


}