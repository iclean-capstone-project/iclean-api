package iclean.code.function.reporttype.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.reporttype.AddReportTypeRequest;
import iclean.code.data.dto.request.reporttype.UpdateReportTypeRequest;
import org.springframework.http.ResponseEntity;

public interface ReportTypeService {
    ResponseEntity<ResponseObject> getAllReportType();

    ResponseEntity<ResponseObject> getReportTypeById(int reportTypeId);

    ResponseEntity<ResponseObject> addReportType(AddReportTypeRequest reportTypeRequest);

    ResponseEntity<ResponseObject> updateReportType(int reportTypeId, UpdateReportTypeRequest reportTypeRequest);

    ResponseEntity<ResponseObject> deleteReportType(int reportTypeId);
}
