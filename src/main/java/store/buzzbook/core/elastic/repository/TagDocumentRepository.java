package store.buzzbook.core.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import store.buzzbook.core.elastic.document.TagDocument;

public interface TagDocumentRepository extends ElasticsearchRepository<TagDocument, Integer> {
}
