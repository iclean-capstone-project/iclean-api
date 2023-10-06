package iclean.code.data.dto.common;

import iclean.code.data.enumjava.DirectionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SortResponse {
    private String sortField;
    private DirectionEnum direction;
}
