package iclean.code.data.dto.response;

import iclean.code.data.dto.common.SortResponse;
import lombok.Data;

import java.util.List;

@Data
public class PageResponseObject {
    private Long offset;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Integer numberOfElements;
    private List<SortResponse> sortBy;
    private List<Object> content;
}
