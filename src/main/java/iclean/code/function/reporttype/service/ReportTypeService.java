package iclean.code.function.reporttype.service;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.reporttype.CreateReportType;
import org.springframework.http.ResponseEntity;

public interface ReportTypeService {
    ResponseEntity<ResponseObject> getReportTypes();

    ResponseEntity<ResponseObject> createReportType(CreateReportType reportTypeRequest);

    ResponseEntity<ResponseObject> deleteReportType(int reportTypeId);
}
