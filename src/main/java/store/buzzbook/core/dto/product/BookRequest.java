package store.buzzbook.core.dto.product;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookRequest {
    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @Nullable
    private String description;

    @NotBlank(message = "ISBN 값을 입력하세요")
    @Size(min = 10, max = 13)
    private String isbn;

    @NotBlank(message = "출판사를 입력하세요")
    @Size(max = 50, message = "출판사 이름은 최대 50자까지 입력 가능합니다")
    private String publisher;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 YYYY-MM-DD 여야 합니다")
    private String publishDate;

    @NotBlank(message = "상품 ID를 입력하세요")
    private Integer productId;

}
