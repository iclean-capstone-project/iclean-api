package iclean.code.function.dashboard.service;

import iclean.code.data.dto.common.ResponseObject;
import org.springframework.http.ResponseEntity;

public interface DashboardService {

    ResponseEntity<ResponseObject> homeDashboard();

    ResponseEntity<ResponseObject> findBookingByDate(String time, String option);
}
