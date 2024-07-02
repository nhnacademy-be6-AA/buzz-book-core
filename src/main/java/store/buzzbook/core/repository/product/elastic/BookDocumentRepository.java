package store.buzzbook.core.repository.product.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import store.buzzbook.core.document.product.BookDocument;

public interface BookDocumentRepository extends ElasticsearchRepository<BookDocument, Long> {
}
