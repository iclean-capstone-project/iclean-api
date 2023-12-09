package iclean.code.function.report.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.CreateReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ReportService {
    ResponseEntity<ResponseObject> getReports(Integer userId, String renterName, Boolean displayAll, Pageable pageable);

    ResponseEntity<ResponseObject> getReportById(Integer reportId);

    ResponseEntity<ResponseObject> createReport(CreateReportRequest reportRequest, Integer renterId);

    ResponseEntity<ResponseObject> updateReport(int reportId, UpdateReportRequest reportRequest, Integer managerId);

    ResponseEntity<ResponseObject> deleteReport(int reportId);
}
