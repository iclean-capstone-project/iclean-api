package iclean.code.function.reporttype.service.impl;

import iclean.code.data.domain.ReportType;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.report.GetReportTypeResponse;
import iclean.code.data.repository.ReportTypeRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.reporttype.service.ReportTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            List<GetReportTypeResponse> responses = reportTypes
                    .stream()
                    .map(transaction -> modelMapper.map(transaction, GetReportTypeResponse.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "All Report type", responses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    private ReportType findReportType(int reportTypeId) {
        return reportTypeRepository.findById(reportTypeId)
                .orElseThrow(() -> new NotFoundException("Report type is not exist"));
    }
}
