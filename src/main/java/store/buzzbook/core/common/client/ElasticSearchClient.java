package store.buzzbook.core.common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "elasticsearchClient", url = "${spring.elasticsearch.uris}")
public interface ElasticSearchClient {

	@GetMapping("/product_index/_search")
	String searchProducts(@RequestParam("q") String query,
		@RequestHeader("Authorization") String authorization);
}