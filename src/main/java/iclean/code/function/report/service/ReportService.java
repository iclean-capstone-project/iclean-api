package iclean.code.function.report.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.AddReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ReportService {
//    ResponseEntity<ResponseObject> getAllReportAsAdminOrManager(Pageable pageable);

    ResponseEntity<ResponseObject> getAllReport(Integer userId, Pageable pageable);

    ResponseEntity<ResponseObject> getReportById(Integer reportId, Integer userId);

    ResponseEntity<ResponseObject> addReport(AddReportRequest reportRequest);

    ResponseEntity<ResponseObject> updateReport(int reportId, UpdateReportRequest reportRequest);

    ResponseEntity<ResponseObject> deleteReport(int reportId);
}
