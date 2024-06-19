package store.buzzbook.core.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import store.buzzbook.core.service.product.BookService;

@SpringBootTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
public class BookControllerTest {

    //private MockMvc mockMvc;
    //private BookService bookService;

//    @Test
//    public void createBookTest() throws Exception {
//        Book book = Book.builder()
//                .id(1L)
//                .title("생체 GPT 관찰일지")
//                .description("이들의 연구는 30년째 계속 되고 있다, 그들의 정체는..")
//                .isbn("1234567890")
//                .publishDate(ZonedDateTime.now())
//                .build();
//    }

}
