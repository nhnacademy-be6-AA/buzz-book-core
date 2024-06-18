package store.buzzbook.core.service.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.buzzbook.core.dto.product.response.BookApiResponse;
import store.buzzbook.core.entity.product.Book;
import store.buzzbook.core.repository.product.BookRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BookSearchService {

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BookRepository bookRepository;

    public BookSearchService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookApiResponse.Item> searchBooks(String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", aladinApiKey)
                .queryParam("Query", query)
                .queryParam("Output", "JS")
                .queryParam("Version", "20131101")
                .toUriString();

        try {
            BookApiResponse apiResponse = restTemplate.getForObject(url, BookApiResponse.class);
            return apiResponse != null ? apiResponse.getItems() : Collections.emptyList();
        } catch (RestClientException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void searchAndSaveBooks(String query) {
        List<BookApiResponse.Item> items = searchBooks(query);
        saveBooksToDatabase(items);
    }

    private void saveBooksToDatabase(List<BookApiResponse.Item> items) {
        List<Book> books = new ArrayList<>();
        for (BookApiResponse.Item item : items) {
            Book book = new Book();
            book.setTitle(item.getTitle());
            book.setDescription(item.getDescription());
            book.setIsbn(item.getIsbn());
            // 필요한 경우 추가 필드 설정
            books.add(book);
        }
        bookRepository.saveAll(books);
    }


}
