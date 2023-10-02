package iclean.code.function.moneyrequest.service.impl;

import iclean.code.data.domain.MoneyRequest;
import iclean.code.data.domain.RejectReason;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.GetMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.UpdateMoneyRequestRequestDTO;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestResponseDTO;
import iclean.code.data.dto.response.rejectreason.GetRejectReasonResponseDTO;
import iclean.code.data.repository.MoneyRequestRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.moneyrequest.service.MoneyRequestService;
import iclean.code.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MoneyRequestServiceImpl implements MoneyRequestService {
    @Autowired
    private MoneyRequestRepository moneyRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getMoneyRequests(GetMoneyRequestRequestDTO request) {
        try {
            List<MoneyRequest> moneyRequests = moneyRequestRepository.findAll();
            List<GetMoneyRequestResponseDTO> responses = moneyRequests
                    .stream()
                    .map(moneyRequest -> modelMapper.map(moneyRequest, GetMoneyRequestResponseDTO.class))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Money Request List",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> getMoneyRequest(Integer id) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestById(id);
            GetMoneyRequestResponseDTO responses = modelMapper.map(moneyRequest, GetMoneyRequestResponseDTO.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Money Request Information",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.toString(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createMoneyRequest(CreateMoneyRequestRequestDTO request) {
        try {
            MoneyRequest moneyRequest = modelMapper.map(request, MoneyRequest.class);
            moneyRequest.setRequestDate(Utils.getDateTimeNow());
            User user = findUserById(request.getUserId());
            moneyRequest.setUser(user);

            moneyRequestRepository.save(moneyRequest);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Money Request Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateMoneyRequest(Integer id, UpdateMoneyRequestRequestDTO request) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestById(id);
            moneyRequest.setProcessDate(Utils.getDateTimeNow());
            moneyRequest = modelMapper.map(request, MoneyRequest.class);
            moneyRequestRepository.save(moneyRequest);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Update Money Request Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> deleteMoneyRequest(Integer id) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestById(id);
            moneyRequestRepository.delete(moneyRequest);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Delete Reject Reason Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }
    private MoneyRequest findMoneyRequestById(Integer id) {
        return moneyRequestRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Money Request ID: %s is not exist", id)));
    }
    private User findUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }
}
