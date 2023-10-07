package iclean.code.data.dto.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PageRequestBuilder {
    public static Pageable buildPageRequest(int page, int size, List<String> sorts) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sort :
             sorts) {
            String[] sortParams = sort.split("_");
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortParams.length == 2) {
                if ("desc".equalsIgnoreCase(sortParams[1])) {
                    direction = Sort.Direction.DESC;
                }
            }

            String sortBy = sortParams[0];

            orders.add(new Sort.Order(direction, sortBy));
        }

        return PageRequest.of(page - 1, size, Sort.by(orders));
    }
    public static Pageable buildPageRequest(int page, int size) {
        return PageRequest.of(page - 1, size);
    }
}

