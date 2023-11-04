package iclean.code.data.dto.common;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class SortRequestBuilder {
    public static Sort buildSortRequest(List<String> sorts) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sort : sorts) {
            String[] sortParams = sort.split("_");
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortParams.length == 2 && "desc".equalsIgnoreCase(sortParams[1])) {
                direction = Sort.Direction.DESC;
            }
            String sortBy = sortParams[0];
            orders.add(new Sort.Order(direction, sortBy));
        }
        return Sort.by(orders);
    }
    public static Sort buildSortRequest() {
        List<Sort.Order> orders = new ArrayList<>();
        return Sort.by(orders);
    }
}
