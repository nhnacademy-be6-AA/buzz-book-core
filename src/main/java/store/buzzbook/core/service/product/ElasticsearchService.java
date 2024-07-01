package store.buzzbook.core.service.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import store.buzzbook.core.common.client.ElasticSearchClient;
import store.buzzbook.core.document.product.ProductDocument;

@Service
public class ElasticsearchService {

	private final ElasticSearchClient elasticSearchClient;

	@Value("${spring.elasticsearch.username}")
	private String username;

	@Value("${spring.elasticsearch.password}")
	private String password;

	public ElasticsearchService(ElasticSearchClient elasticSearchClient) {
		this.elasticSearchClient = elasticSearchClient;
	}

	public List<ProductDocument> searchProducts(String query) throws JsonProcessingException {
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		String response = elasticSearchClient.searchProducts(query,"Basic "+token);

		//Json응답 -> ProductDocument 리스트로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		List<ProductDocument> products = objectMapper.readValue(response, new TypeReference<List<ProductDocument>>() {});
		return products;
	}
}