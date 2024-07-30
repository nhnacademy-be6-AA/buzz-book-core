package store.buzzbook.core.elastic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import store.buzzbook.core.elastic.client.ElasticSearchClient;
import store.buzzbook.core.elastic.document.BookDocument;

@Service
@RequiredArgsConstructor
public class ElasticsearchService {

	private final ElasticSearchClient elasticSearchClient;
	private final ObjectMapper objectMapper;

	@Value("${spring.elasticsearch.username}")
	private String username;

	@Value("${spring.elasticsearch.password}")
	private String password;


	public List<BookDocument> searchProducts(String query) throws JsonProcessingException {
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		String response = elasticSearchClient.searchProducts(query, "Basic " + token);

		// JSON 응답 -> BookDocument 리스트로 변환

		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		JsonNode rootNode = objectMapper.readTree(response);
		JsonNode hitsNode = rootNode.path("hits").path("hits");

		List<BookDocument> books = new ArrayList<>();
		for (JsonNode hitNode : hitsNode) {
			JsonNode sourceNode = hitNode.path("_source");
			BookDocument book = objectMapper.treeToValue(sourceNode, BookDocument.class);
			books.add(book);
		}

		return books.stream()
			.map(book -> new BookDocument(
				book.getBookId(),
				book.getProductId(),
				book.getIsbn(),
				book.getBookTitle(),
				book.getDescription(),
				book.getForwardDate(),
				book.getAuthors()
			))
			.collect(Collectors.toList());
	}
}
