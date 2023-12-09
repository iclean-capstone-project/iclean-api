package iclean.code.function.reporttype.service.impl;

import iclean.code.data.domain.ReportType;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.report.GetReportTypeResponse;
import iclean.code.data.dto.request.reporttype.CreateReportType;
import iclean.code.data.repository.ReportTypeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.reporttype.service.ReportTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportTypeServiceImpl implements ReportTypeService {

    @Autowired
    private ReportTypeRepository reportTypeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<ResponseObject> getReportTypes() {
        try {
            List<ReportType> reportTypes = reportTypeRepository.findAll();
            GetReportTypeResponse reportTypeResponse = modelMapper.map(reportTypes, GetReportTypeResponse.class);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Report type", reportTypeResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createReportType(CreateReportType reportTypeRequest) {
        try {
            ReportType reportType = modelMapper.map(reportTypeRequest, ReportType.class);
            reportType.setReportName(reportTypeRequest.getReportDetail());
            reportTypeRepository.save(reportType);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create Report type Successfully!", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteReportType(int reportTypeId) {
        try {
            ReportType optionalReportType = findReportType(reportTypeId);

            reportTypeRepository.delete(optionalReportType);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Report type Successfully!", null));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private ReportType findReportType(int reportTypeId) {
        return reportTypeRepository.findById(reportTypeId)
                .orElseThrow(() -> new NotFoundException("Report type is not exist"));
    }
}
