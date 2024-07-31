package store.buzzbook.core.elastic.service;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.elastic.client.ElasticSearchClient;
import store.buzzbook.core.elastic.document.BookDocument;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.repository.product.BookRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DataTransferService {

	private final BookRepository bookRepository;
	private final ElasticSearchClient elasticSearchClient;
	private final ObjectMapper objectMapper;

	@Value("${spring.elasticsearch.username}")
	private String username;
	@Value("${spring.elasticsearch.password}")
	private String password;


	// MySQL에서 Elasticsearch로 데이터 동기화
	public Long mySqlDataTransferToElastic() {
		List<Book> books = bookRepository.findAll();

		for (Book book : books) {
			try {
				BookDocument bookDocument = convertToBookDocument(book);
				String jsonString = objectMapper.writeValueAsString(bookDocument);
				elasticSearchClient.indexDocument("aa-bb_book_index", jsonString);
			} catch (JsonProcessingException e) {
				e.printStackTrace(); // 에러 처리
			}
		}
		return (long) books.size(); // 총 전송된 데이터 갯수 반환
	}

	private BookDocument convertToBookDocument(Book book) {
		return new BookDocument(
			book.getId(),
			book.getProduct() != null ? book.getProduct().getId() : 0,
			book.getIsbn(),
			book.getTitle(),
			book.getDescription(),
			book.getPublishDate(),
			book.getBookAuthors().stream()
				.map(bookAuthor -> bookAuthor.getAuthor().getName())
				.toList()
		);
	}

	private String getBasicAuth()
	{
		String auth = username + ":" + password;
		return Base64.getEncoder().encodeToString(auth.getBytes());
	}

}