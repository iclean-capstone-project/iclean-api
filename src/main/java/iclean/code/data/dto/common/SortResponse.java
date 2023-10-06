package iclean.code.data.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class SortResponse {
    private String sortField;
    private Sort.Direction direction;
}
