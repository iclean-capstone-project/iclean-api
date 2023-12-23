package iclean.code.function.rejectionreason.service.impl;

import iclean.code.data.domain.RejectionReason;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.rejectionreason.GetRejectionReasonResponse;
import iclean.code.data.repository.RejectionReasonRepository;
import iclean.code.function.rejectionreason.service.RejectReasonService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RejectReasonServiceImpl implements RejectReasonService {
    @Autowired
    private RejectionReasonRepository rejectionReasonRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getRejectionReasons() {
        try {
            List<RejectionReason> rejectionReasons = rejectionReasonRepository.findAll();
            List<GetRejectionReasonResponse> responses = rejectionReasons
                    .stream()
                    .map(rejectionReason -> modelMapper.map(rejectionReason, GetRejectionReasonResponse.class))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Reject Reason List",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
}
