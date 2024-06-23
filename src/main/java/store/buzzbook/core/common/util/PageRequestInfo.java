package store.buzzbook.core.common.util;

import lombok.Getter;

@Getter
public class PageRequestInfo {
    private Integer page;
    private Integer size;
    private String[] sortBy;
    private Boolean[] sortDesc;
}
