package store.buzzbook.core.service.product;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.buzzbook.core.dto.product.BookApiRequest;
import store.buzzbook.core.entity.product.Author;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.entity.product.BookAuthor;
import store.buzzbook.core.entity.product.Category;
import store.buzzbook.core.entity.product.Product;
import store.buzzbook.core.entity.product.Publisher;
import store.buzzbook.core.repository.product.AuthorRepository;
import store.buzzbook.core.repository.product.BookAuthorRepository;
import store.buzzbook.core.repository.product.BookRepository;
import store.buzzbook.core.repository.product.CategoryRepository;
import store.buzzbook.core.repository.product.ProductRepository;
import store.buzzbook.core.repository.product.PublisherRepository;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookSearchService {

	private final CharacterEncodingFilter characterEncodingFilter;
	private final RestTemplate restTemplate = new RestTemplate();
	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final PublisherRepository publisherRepository;
	private final ProductRepository productRepository;
	private final BookAuthorRepository bookAuthorRepository;
	private final CategoryRepository categoryRepository;
	@Value("${aladin.api.key}")
	private String aladinApiKey;
	// private final ProductDocumentRepository productDocumentRepository;

	public List<BookApiRequest.Item> searchBooks(String query) {

		URI url = UriComponentsBuilder.fromHttpUrl("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
			.queryParam("ttbkey", aladinApiKey)
			.queryParam("Query", URLEncoder.encode(query, StandardCharsets.UTF_8))
			.queryParam("QueryType", "Title")
			.queryParam("Output", "JS")
			.queryParam("Version", "20131101")
			.build(true)
			.toUri();

		try {
			BookApiRequest apiResponse = restTemplate.getForObject(url, BookApiRequest.class);
			if (apiResponse != null && apiResponse.getItems() != null) {
				// 각 아이템의 커버 이미지 URL을 큰 이미지로 변경
				apiResponse.getItems().forEach(item -> {
					if (item.getCover() != null) {
						item.setCover(item.getCover().replace("coversum", "cover500"));
					}
				});
				return apiResponse.getItems();
			} else {
				return List.of();
			}
		} catch (RestClientException e) {
			e.printStackTrace();
			return List.of();
		}
	}

	public void searchAndSaveBooks(String query) {
		List<BookApiRequest.Item> items = searchBooks(query);
		saveBooksToDatabase(items);
	}

	@Transactional
	public void saveBooksToDatabase(List<BookApiRequest.Item> items) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		for (BookApiRequest.Item item : items) {
			if (bookRepository.existsByIsbn(item.getIsbn())) {
				// ISBN을 기준으로 이미 존재하는 책은 스킵
				log.info("이미 존재하는 책 스킵: {}", item.getTitle());
				continue;
			}

			try {
				// 카테고리 저장
				List<String> categoryParts = List.of(item.getCategory().split(">"));
				Category parentCategory = null;
				for (String categoryName : categoryParts) {
					categoryName = categoryName.strip();
					Category category = categoryRepository.findByName(categoryName);
					if (category == null) {
						category = new Category(categoryName, parentCategory);
						categoryRepository.save(category);
					}
					parentCategory = category;
				}

				// 출판사 저장
				Publisher publisher = publisherRepository.findByName(item.getPublisher());
				if (publisher == null) {
					publisher = new Publisher(item.getPublisher());
					publisherRepository.save(publisher);
				}

				// 도서 저장
				Book book = new Book(
					item.getTitle(),
					item.getDescription(),
					item.getIsbn(),
					publisher,
					item.getPubDate()
				);
				book = bookRepository.save(book);

				// 기존 Product 확인
				Product product = productRepository.findByThumbnailPath(item.getCover());
				if (product == null) {
					// 새로운 Product 생성 및 저장
					product = Product.builder()
						.stock(item.getStock() != null ? Integer.parseInt(item.getStock()) : 1)
						.productName(item.getTitle())
						.description(item.getDescription())
						.price(item.getPriceStandard())
						.forwardDate(LocalDate.parse(item.getPubDate(), dateFormatter))
						.score(item.getCustomerReviewRank())
						.thumbnailPath(item.getCover())
						.stockStatus(Product.StockStatus.SALE)
						.category(parentCategory)
						.build();
					product = productRepository.save(product);
				}

				book.setProduct(product);
				bookRepository.save(book);

				// 저자 저장 및 도서별 저자 저장
				for (String authorName : item.getAuthor().split(",")) {
					Author author = authorRepository.findByName(authorName.trim());
					if (author == null) {
						author = new Author(authorName.trim());
						authorRepository.save(author);
					}
					BookAuthor bookAuthor = new BookAuthor(author, book);
					bookAuthorRepository.save(bookAuthor);
				}

			} catch (DateTimeParseException e) {
				log.error("날짜 파싱 오류: {}", item.getTitle(), e);
			} catch (NumberFormatException e) {
				log.error("'상품' 정보 저장 중 오류 발생: {}", item.getTitle(), e);
			} catch (Exception e) {
				log.error("'도서' 저장 중 오류 발생: {}", item.getTitle(), e);
			}
		}
	}

}
