package store.buzzbook.core.elastic.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "elasticsearchClient", url = "${spring.elasticsearch.uris}")
public interface ElasticSearchClient {

	@GetMapping("/aa-bb_book_index/_search")
	String searchProducts(@RequestParam("q") String query,
		@RequestHeader("Authorization") String authorization,
		@RequestParam("from") int from,
		@RequestParam("size") int size);


	// @PostMapping("/aa-bb_book_index/_search")
	// String suggest(@RequestBody String suggestQuery,
	// 	@RequestHeader("Authorization") String authorization);

	@PostMapping("/aa-bb_book_index/_doc")
	void indexDocument(@RequestBody String document,
		@RequestHeader("Authorization") String authorization);


}
