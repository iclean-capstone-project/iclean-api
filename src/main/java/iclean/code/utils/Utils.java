package iclean.code.utils;

import iclean.code.data.dto.common.SortResponse;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.enumjava.DirectionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Utils {
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static LocalDateTime getDateTimeNow() {
        ZoneId gmtPlus7Zone = ZoneId.of("GMT+7");
        return LocalDateTime.now(gmtPlus7Zone);
    }

    public static PageResponseObject convertToPageResponse(Page page) {
        PageResponseObject pageResponseObject = new PageResponseObject();
        pageResponseObject.setOffset(page.getPageable().getOffset());
        pageResponseObject.setPageNumber(page.getPageable().getPageNumber());
        pageResponseObject.setPageSize(page.getPageable().getPageSize());
        pageResponseObject.setTotalPages(page.getTotalPages());
        pageResponseObject.setTotalElements(page.getTotalElements());
        pageResponseObject.setNumberOfElements(page.getNumberOfElements());
        List<SortResponse> sortResponseList = new ArrayList<>();
        SortResponse sortResponse = new SortResponse("description", DirectionEnum.ASC);
        sortResponseList.add(sortResponse);
        pageResponseObject.setSortBy(sortResponseList);
        pageResponseObject.setContent(page.getContent());
        return pageResponseObject;
    }
}
