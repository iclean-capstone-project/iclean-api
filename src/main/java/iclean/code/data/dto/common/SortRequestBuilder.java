package iclean.code.data.dto.common;

import iclean.code.utils.anotation.SortValue;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
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

    public static Sort buildSortRequest(List<String> sorts, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Sort.Order> orders = new ArrayList<>();
        for (String sort : sorts) {
            String[] sortParams = sort.split("_");
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortParams.length == 2 && "desc".equalsIgnoreCase(sortParams[1])) {
                direction = Sort.Direction.DESC;
            }
            String sortBy = sortParams[0];
            boolean foundField = false;
            for (Field field : fields) {
                if (field.isAnnotationPresent(SortValue.class) && field.getName().equalsIgnoreCase(sortBy)) {
                    SortValue sortValue = field.getAnnotation(SortValue.class);
                    orders.add(new Sort.Order(direction, sortValue.value()));
                    foundField = true;
                    break;
                }
            }
            if (!foundField) {
                orders.add(new Sort.Order(direction, sortBy));
            }
        }
        return Sort.by(orders);
    }
}
