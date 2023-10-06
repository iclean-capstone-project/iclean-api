package iclean.code.utils;

import iclean.code.data.dto.common.SortResponse;
import iclean.code.data.dto.response.PageResponseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static LocalDateTime getDateTimeNow() {
        ZoneId gmtPlus7Zone = ZoneId.of("GMT+7");
        return LocalDateTime.now(gmtPlus7Zone);
    }

    public static String removeSpace(String value) {
        return value.replace(" ", "");
    }

    public static String removeAccentMarksForSearching(String input) {
        if (input == null)
            return "%%";
        return "%" + Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") + "%";
    }

    public static String subStringLastIndex(String value, String str) {
        if (isNullOrEmpty(value)) return "";
        return value.substring(value.lastIndexOf(str) + 1);
    }

    public static PageResponseObject convertToPageResponse(Page page, List<?> content) {
        PageResponseObject pageResponseObject = new PageResponseObject();
        pageResponseObject.setOffset(page.getPageable().getOffset());
        pageResponseObject.setPageNumber(page.getPageable().getPageNumber());
        pageResponseObject.setPageSize(page.getPageable().getPageSize());
        pageResponseObject.setTotalPages(page.getTotalPages());
        pageResponseObject.setTotalElements(page.getTotalElements());
        pageResponseObject.setNumberOfElements(page.getNumberOfElements());
        List<SortResponse> sortResponseList = null;
        if (page.getPageable().getSort().isSorted()) {
            sortResponseList = new ArrayList<>();
            for (Sort.Order order:
                    page.getPageable().getSort()) {
                sortResponseList.add(new SortResponse(order.getProperty(), order.getDirection()));
            }
        }
        pageResponseObject.setSortBy(sortResponseList);
        pageResponseObject.setContent(content);
        return pageResponseObject;
    }
}
