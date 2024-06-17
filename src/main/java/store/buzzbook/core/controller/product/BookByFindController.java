package store.buzzbook.core.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import store.buzzbook.core.dto.product.response.BookApiResponse;

import java.util.Collections;
import java.util.List;

// open api 테스트 삼아서 해보는중
@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Management API", description = "도서 정보 검색 API")
public class BookByFindController {

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    @Operation(summary = "Get books from Aladin API", description = "여기에 쿼리문에 검색하고자 하는 이름을 입력하기")
    public List<BookApiResponse.Item> getBooks(@RequestParam(required = false, defaultValue = "") String query) {
        String url = UriComponentsBuilder.fromHttpUrl("https://www.aladin.co.kr/ttb/api/ItemSearch.aspx")
                .queryParam("ttbkey", aladinApiKey)
                .queryParam("Query", query)
                .queryParam("Output", "JS")
                .queryParam("Version", "20131101") //버전명
                .toUriString();

        try {
            BookApiResponse apiResponse = restTemplate.getForObject(url, BookApiResponse.class);
            return apiResponse != null ? apiResponse.getItems() : Collections.emptyList();
        } catch (RestClientException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}