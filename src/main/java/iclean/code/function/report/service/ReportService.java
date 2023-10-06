package iclean.code.function.report.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.report.AddReportRequest;
import iclean.code.data.dto.request.report.UpdateReportRequest;
import org.springframework.http.ResponseEntity;

public interface ReportService {
    ResponseEntity<ResponseObject> getAllReport();

    ResponseEntity<ResponseObject> getReportById(int reportId);

    ResponseEntity<ResponseObject> addReport(AddReportRequest reportRequest);

    ResponseEntity<ResponseObject> updateReport(int reportId, UpdateReportRequest reportRequest);

    ResponseEntity<ResponseObject> deleteReport(int reportId);
}
