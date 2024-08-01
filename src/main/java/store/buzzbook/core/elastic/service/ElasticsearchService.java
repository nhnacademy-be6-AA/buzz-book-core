package store.buzzbook.core.elastic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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

	private void configureObjectMapper() {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	private String createAuthToken() {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	public Page<BookDocument> searchProducts(String query, int pageNo, int pageSize) throws JsonProcessingException {
		configureObjectMapper();
		String token = createAuthToken();
		String response = elasticSearchClient.searchProducts(query, token, pageNo, pageSize);

		JsonNode rootNode = objectMapper.readTree(response);
		JsonNode hitsNode = rootNode.path("hits").path("hits");
		JsonNode totalNode = rootNode.path("hits").path("total").path("value");

		List<BookDocument> books = new ArrayList<>();
		for (JsonNode hitNode : hitsNode) {
			JsonNode sourceNode = hitNode.path("_source");
			BookDocument book = objectMapper.treeToValue(sourceNode, BookDocument.class);
			books.add(book);
		}

		long totalHits = totalNode.asLong();
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);  // pageNo는 1부터 시작
		return new PageImpl<>(books, pageable, totalHits);
	}

	// public List<String> getAutocompleteSuggestions(String query) {
	// 	try {
	// 		configureObjectMapper();
	// 		String token = createAuthToken();
	// 		String suggestQuery = buildSuggestQuery(query);  // 자동 완성을 위한 요청 본문 생성
	// 		String response = elasticSearchClient.suggest(suggestQuery, token);
	//
	// 		List<String> suggestions = new ArrayList<>();
	// 		JsonNode rootNode = objectMapper.readTree(response);
	// 		JsonNode suggestNode = rootNode.path("suggest").path("autocomplete");
	// 		for (JsonNode suggestion : suggestNode) {
	// 			suggestions.add(suggestion.path("text").asText());
	// 		}
	// 		return suggestions;
	// 	} catch (JsonProcessingException e) {
	// 		e.printStackTrace(); // 실제로는 로깅을 사용하여 오류를 기록하는 것이 좋습니다.
	// 		return Collections.emptyList(); // 오류 발생 시 빈 리스트 반환
	// 	}
	// }

	private String buildSuggestQuery(String query) {
		// 엘라스틱 서치의 suggest 요청 본문을 JSON 형식으로 작성
		return "{ \"suggest\": { \"autocomplete\": { \"prefix\": \"" + query + "\", \"completion\": { \"field\": \"suggest\" } } } }";
	}
}
