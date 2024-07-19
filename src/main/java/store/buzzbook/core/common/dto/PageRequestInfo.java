package store.buzzbook.core.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageRequestInfo {
    private Integer page;
    private Integer size;
}
