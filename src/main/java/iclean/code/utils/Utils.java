package iclean.code.utils;

import iclean.code.data.dto.common.SortResponse;
import iclean.code.data.dto.request.workschedule.DateTimeRange;
import iclean.code.data.dto.request.workschedule.TimeRange;
import iclean.code.data.dto.response.PageResponseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static LocalDateTime getLocalDateTimeNow() {
        ZoneId gmtPlus7Zone = ZoneId.of("GMT+7");
        return LocalDateTime.now(gmtPlus7Zone);
    }

    public static ZoneId getZoneId() {
        return ZoneId.of("GMT+7");
    }

    public static boolean isBeforeOrEqual(LocalTime value, LocalTime other) {
        return !value.isAfter(other);
    }

    public static boolean isAfterOrEqual(LocalTime value, LocalTime other) {
        return !value.isBefore(other);
    }

    public static double minusLocalTime(LocalTime value, LocalTime other) {
        Duration duration = Duration.between(value, other);
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.UP);
        return Double.parseDouble(df.format(duration.toSeconds() / 3600.0));
    }

    public static long minusLocalTimeAsMinutes(LocalTime value, LocalTime other) {
        Duration duration = Duration.between(value, other);
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.UP);
        return Long.parseLong(df.format(duration.toMinutes()));
    }

    public static long minusLocalDateTime(LocalDateTime value, LocalDateTime other) {
        long valueLong = value.atZone(getZoneId()).toInstant().toEpochMilli();
        long otherLong = other.atZone(getZoneId()).toInstant().toEpochMilli();
        return otherLong - valueLong;
    }

    public static boolean isWithinMinutes(long different, long minutes) {
        long difference = Math.abs(different);
        long minutesInMillis = minutes * 60 * 1000;
        return difference <= minutesInMillis;
    }

    public static String generateRandomCode() {
        LocalDateTime now = getLocalDateTimeNow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        String formattedDateTime = now.format(formatter);
        Random random = new Random();
        int randomValue = random.nextInt(10000);
        return formattedDateTime + String.format("%04d", randomValue);
    }

    public static boolean isLateMinutes(long different, long minutes) {
        long minutesInMillis = minutes * 60 * 1000;
        return different > minutesInMillis;
    }

    public static boolean isSoonMinutes(long different, long minutes) {
        long difference = Math.abs(different);
        long minutesInMillis = minutes * 60 * 1000;
        return difference > minutesInMillis;
    }

    public static LocalTime plusLocalTime(LocalTime value, double hoursToAdd) {
        int wholeHours = (int) hoursToAdd;
        int minutesToAdd = (int) ((hoursToAdd - wholeHours) * 60);
        int secondsToAdd = (int) ((hoursToAdd - wholeHours) * 60) - minutesToAdd;
        return value.plusHours(wholeHours).plusMinutes(minutesToAdd).plusSeconds(secondsToAdd);
    }

    public static LocalDateTime plusLocalDateTime(LocalDateTime value, double hoursToAdd) {
        int wholeHours = (int) hoursToAdd;
        int minutesToAdd = (int) ((hoursToAdd - wholeHours) * 60);
        int secondsToAdd = (int) ((hoursToAdd - wholeHours) * 60) - minutesToAdd;
        return value.plusHours(wholeHours).plusMinutes(minutesToAdd).plusSeconds(secondsToAdd);
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

    public static Double roundingNumber(Double number, Double divisor, RoundingMode roundingMode) {
        int result = 0;
        switch (roundingMode) {
            case DOWN:
                result = (int) ((number / divisor) * divisor);
                break;
            case UP:
                result = (int) ((number / divisor) * divisor + divisor);
                break;
        }
        return (double) result;
    }

    public static String getLocalDateAsString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (Objects.isNull(localDate)) {
            return null;
        }
        return localDate.format(formatter);
    }

    public static String getLocalTimeAsString(LocalTime localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (Objects.isNull(localDate)) {
            return null;
        }
        return localDate.format(formatter);
    }

    public static LocalDate convertStringToLocalDate(String dataString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dataString, formatter);
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
        pageResponseObject.setPageNumber(page.getPageable().getPageNumber() + 1);
        pageResponseObject.setPageSize(page.getPageable().getPageSize());
        pageResponseObject.setTotalPages(page.getTotalPages());
        pageResponseObject.setTotalElements(page.getTotalElements());
        pageResponseObject.setNumberOfElements(page.getNumberOfElements());
        List<SortResponse> sortResponseList = null;
        if (page.getPageable().getSort().isSorted()) {
            sortResponseList = new ArrayList<>();
            for (Sort.Order order :
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

    public static boolean hasOverlapTime(List<TimeRange> timeRanges) {
        timeRanges.sort(Comparator.comparing(TimeRange::getStartTime));
        for (int i = 0; i < timeRanges.size() - 1; i++) {
            TimeRange range1 = timeRanges.get(i);
            TimeRange range2 = timeRanges.get(i + 1);

            if (range1.getEndTime().isAfter(range2.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasOverlapTime(DateTimeRange timeRanges, DateTimeRange otherRanges) {
        return (timeRanges.getStartDateTime().isBefore(otherRanges.getEndDateTime()) || timeRanges.getStartDateTime().isEqual(otherRanges.getEndDateTime())) &&
                (timeRanges.getEndDateTime().isAfter(otherRanges.getStartDateTime()) || timeRanges.getEndDateTime().isEqual(otherRanges.getStartDateTime()));
    }

}
