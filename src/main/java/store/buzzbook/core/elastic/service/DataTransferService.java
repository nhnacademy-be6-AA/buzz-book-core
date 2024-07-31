package store.buzzbook.core.elastic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.elastic.client.ElasticSearchClient;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.repository.product.BookRepository;

@Service
@RequiredArgsConstructor
public class DataTransferService {

	private final BookRepository bookRepository;
	private final ElasticSearchClient elasticSearchClient; // Elasticsearch에 데이터 전송을 위한 클라이언트
	private final ObjectMapper objectMapper;

	// MySQL에서 Elasticsearch로 데이터 동기화
	public Long mySqlDataTransferToElastic() {
		List<Book> books = bookRepository.findAll();

		for (Book book : books) {
			try {
				String jsonString = objectMapper.writeValueAsString(book);
				// Elastic Search 에 데이터 전송
				elasticSearchClient.indexDocument("aa-bb_book_index", jsonString);
			} catch (JsonProcessingException e) {
				e.printStackTrace(); // 에러 처리
			}
		}
		return (long) books.size(); // 총 전송된 데이터 갯수 반환
	}
}