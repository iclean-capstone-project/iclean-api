package iclean.code.utils;

import iclean.code.data.dto.common.SortResponse;
import iclean.code.data.dto.response.PageResponseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nullable;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static LocalDateTime getDateTimeNow() {
        ZoneId gmtPlus7Zone = ZoneId.of("GMT+7");
        return LocalDateTime.now(gmtPlus7Zone);
    }

    public static String convertToTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        input = input.replace("  ", " ");
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                result.append(word.substring(1).toLowerCase());
                result.append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String getDateTimeNowAsString() {
        ZoneId gmtPlus7Zone = ZoneId.of("GMT+7");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return LocalDateTime.now(gmtPlus7Zone).format(formatter);
    }

    public static String getLocalDateAsString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (Objects.isNull(localDate)) {
            return null;
        }
        return localDate.format(formatter);
    }

    public static LocalDate convertStringToLocalDateTime(String dataString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dataString, formatter);
    }

    public static Double minusLocalTime(LocalTime start, LocalTime end) {
        double numberHours = start.until(end, ChronoUnit.HOURS);
        double numberMinutes = start.until(end, ChronoUnit.MINUTES);
        return numberHours + numberMinutes / 60;
    }

    public static String removeSpace(String value) {
        if (isNullOrEmpty(value)) return null;
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

    public static PageResponseObject convertToPageResponse(Page page, @Nullable List<?> content) {
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
        if (!ObjectUtils.isEmpty(content))
            pageResponseObject.setContent(content);
        else
            pageResponseObject.setContent(page.getContent());
        return pageResponseObject;
    }
}
