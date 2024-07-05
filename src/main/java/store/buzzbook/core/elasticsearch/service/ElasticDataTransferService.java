// package store.buzzbook.core.elasticsearch.elastic.service;
//
// import java.util.List;
//
// import org.springframework.stereotype.Service;
//
// import lombok.RequiredArgsConstructor;
// import store.buzzbook.core.elasticsearch.elastic.document.AuthorDocument;
// import store.buzzbook.core.elasticsearch.elastic.document.BookDocument;
// import store.buzzbook.core.elasticsearch.elastic.document.CategoryDocument;
// import store.buzzbook.core.elasticsearch.elastic.document.ProductDocument;
// import store.buzzbook.core.elasticsearch.elastic.document.PublisherDocument;
// import store.buzzbook.core.elasticsearch.elastic.document.TagDocument;
// import store.buzzbook.core.elasticsearch.elastic.repository.AuthorDocumentRepository;
// import store.buzzbook.core.elasticsearch.elastic.repository.BookDocumentRepository;
// import store.buzzbook.core.elasticsearch.elastic.repository.CategoryDocumentRepository;
// import store.buzzbook.core.elasticsearch.elastic.repository.ProductDocumentRepository;
// import store.buzzbook.core.elasticsearch.elastic.repository.PublisherDocumentRepository;
// import store.buzzbook.core.elasticsearch.elastic.repository.TagDocumentRepository;
// import store.buzzbook.core.entity.product.Author;
// import store.buzzbook.core.entity.product.Book;
// import store.buzzbook.core.entity.product.Category;
// import store.buzzbook.core.entity.product.Product;
// import store.buzzbook.core.entity.product.Publisher;
// import store.buzzbook.core.entity.product.Tag;
// import store.buzzbook.core.repository.product.AuthorRepository;
// import store.buzzbook.core.repository.product.BookRepository;
// import store.buzzbook.core.repository.product.CategoryRepository;
// import store.buzzbook.core.repository.product.ProductRepository;
// import store.buzzbook.core.repository.product.PublisherRepository;
// import store.buzzbook.core.repository.product.TagRepository;
//
// @RequiredArgsConstructor
// @Service
// public class ElasticDataTransferService {
//
// 	private final AuthorRepository authorRep;
// 	private final BookRepository bookRep;
// 	private final CategoryRepository categoryRep;
// 	private final ProductRepository productRep;
// 	private final PublisherRepository publisherRep;
// 	private final TagRepository tagRep;
//
// 	private final AuthorDocumentRepository authorDocRep;
// 	private final BookDocumentRepository bookDocRep;
// 	private final CategoryDocumentRepository categoryDocRep;
// 	private final ProductDocumentRepository productDocRep;
// 	private final PublisherDocumentRepository publisherDocRep;
// 	private final TagDocumentRepository tagDocRep;
//
// 	public long mySqlDataTransferToElastic(){
//
// 		long allDataSize = 0;
// 		List<Author> allAuthors = authorRep.findAll();
// 		authorDocRep.saveAll(allAuthors.stream().map(AuthorDocument::new).toList());
// 		allDataSize += allAuthors.size();
//
// 		List<Book> allBooks = bookRep.findAll();
// 		bookDocRep.saveAll(allBooks.stream().map(BookDocument::new).toList());
// 		allDataSize += allBooks.size();
//
// 		List<Category> allCategories = categoryRep.findAll();
// 		categoryDocRep.saveAll(allCategories.stream().map(CategoryDocument::new).toList());
// 		allDataSize += allCategories.size();
//
// 		List<Product> allProducts = productRep.findAll();
// 		productDocRep.saveAll(allProducts.stream().map(ProductDocument::new).toList());
// 		allDataSize += allProducts.size();
//
// 		List<Publisher> allPublishers = publisherRep.findAll();
// 		publisherDocRep.saveAll(allPublishers.stream().map(PublisherDocument::new).toList());
// 		allDataSize += allPublishers.size();
//
// 		List<Tag> allTags = tagRep.findAll();
// 		tagDocRep.saveAll(allTags.stream().map(TagDocument::new).toList());
// 		allDataSize += allTags.size();
//
// 		return allDataSize;
// 	}
// }
