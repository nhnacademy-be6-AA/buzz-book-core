package store.buzzbook.core.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.buzzbook.core.dto.product.response.BookApiResponse;
import store.buzzbook.core.entity.product.*;
import store.buzzbook.core.repository.product.*;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookSearchService {

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final ProductRepository productRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final CategoryRepository categoryRepository;

    public List<BookApiResponse.Item> searchBooks(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", aladinApiKey)
                .queryParam("Query", query)
                .queryParam("QueryType","Title")
                .queryParam("Output", "JS")
                .queryParam("Version", "20131101")
                .toUriString();

        try {
            BookApiResponse apiResponse = restTemplate.getForObject(url, BookApiResponse.class);
            return apiResponse != null ? apiResponse.getItems() : List.of();
        } catch (RestClientException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void searchAndSaveBooks(String query) {
        List<BookApiResponse.Item> items = searchBooks(query);
        saveBooksToDatabase(items);
    }

    @Transactional
    public void saveBooksToDatabase(List<BookApiResponse.Item> items) {
        for (BookApiResponse.Item item : items) {
            // 카테고리 저장
            String fullCategoryName = item.getCategory();
            if (fullCategoryName == null || fullCategoryName.isEmpty()) {
                System.err.println("Category 값이 null 이거나 empty 인 item: " + item.getTitle());
                continue;
            }

            String[] categoryParts = fullCategoryName.split(">");
            if (categoryParts.length < 2) {
                System.err.println("카테고리 정보가 부족한 도서입니다 : " + item.getTitle());
                continue;
            }

            // 카테고리 분류 최대 3개
            String mainCategoryName = categoryParts[0].trim();
            String subCategoryName1 = categoryParts[1].trim();
            String subCategoryName2 = categoryParts.length > 2 ? categoryParts[2].trim() : null;

            // main
            Category mainCategory = categoryRepository.findByName(mainCategoryName);
            if (mainCategory == null) {
                mainCategory = new Category(mainCategoryName, null);
                categoryRepository.save(mainCategory);
            }

            // sub1
            Category subCategory1 = categoryRepository.findByName(subCategoryName1);
            if (subCategory1 == null) {
                subCategory1 = new Category(subCategoryName1, mainCategory);
                categoryRepository.save(subCategory1);
            }

            // sub2
            Category subCategory2 = null;
            if (subCategoryName2 != null) {
                subCategory2 = categoryRepository.findByName(subCategoryName2);
                if (subCategory2 == null) {
                    subCategory2 = new Category(subCategoryName2, subCategory1);
                    categoryRepository.save(subCategory2);
                }
            }

            // 출판사 저장
            Publisher publisher = publisherRepository.findByName(item.getPublisher());
            if (publisher == null) {
                publisher = new Publisher(item.getPublisher());
                publisherRepository.save(publisher);
            }

            // 도서 저장
            Book book;
            try {
                book = new Book(
                        item.getTitle(),
                        item.getDescription(),
                        item.getIsbn(),
                        publisher,
                        item.getPubDate()
                );

                book = bookRepository.save(book);
            } catch (Exception e) {
                System.err.println("도서 저장 중 오류 발생: " + item.getTitle());
                e.printStackTrace();
                continue;
            }

            // 상품 정보 저장 및 도서와 연결
            try {
                int stock = item.getStock() != null ? Integer.parseInt(item.getStock()) : 0;
                Category category = subCategory2 != null ? subCategory2 : subCategory1;

                // 기존 Product 확인 및 삭제
                Product existingProduct = productRepository.findByBookId(book.getId());
                if (existingProduct != null) {
                    productRepository.delete(existingProduct);
                }

                // 새로운 Product 생성 및 저장
                Product product = Product.builder()
                        .stock(stock)
                        .price(new BigDecimal(item.getPricesales()))
                        .forward_date(item.getPubDate())
                        .score(item.getCustomerReviewRank())
                        .thumbnail_path(item.getCover())
                        .category(category)
                        .book(book)
                        .build();

                productRepository.save(product);

            } catch (Exception e) {
                System.err.println("상품 정보 저장 중 오류 발생: " + item.getTitle());
                e.printStackTrace();
                continue;
            }

            // 저자 저장 및 도서별 저자 저장
            for (String authorName : item.getAuthor().split(",")) {
                Author author = authorRepository.findByName(authorName.trim());
                if (author == null) {
                    author = new Author(authorName.trim());
                    authorRepository.save(author);
                }

                BookAuthor bookAuthor = new BookAuthor(null, author, book);
                bookAuthorRepository.save(bookAuthor);
            }
        }
    }
}
