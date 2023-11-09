package iclean.code.utils.anotation;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
public class SortValueFields {

    public List<String> getSortValueFields(Object obj) {
        Class<?> clazz = obj.getClass();
        List<String> sortField = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            SortValue annotation = field.getAnnotation(SortValue.class);
            if (annotation != null) {
                sortField.add(annotation.value());
            }
            sortField.add(field.getName());
        }
        return sortField;
    }
}
