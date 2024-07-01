package store.buzzbook.core.service.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;
import store.buzzbook.core.common.client.ElasticSearchClient;

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

	public String searchProducts(String query) {
		String token = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		return elasticSearchClient.searchProducts(query, "Basic " + token);
	}
}