package iclean.code.function.moneyrequest.service.impl;

import iclean.code.config.MessageVariable;
import iclean.code.data.domain.MoneyRequest;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequest;
import iclean.code.data.dto.request.moneyrequest.ValidateMoneyRequest;
import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.data.dto.request.transaction.TransactionRequest;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestResponse;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestUserResponse;
import iclean.code.data.enumjava.MoneyRequestEnum;
import iclean.code.data.enumjava.MoneyRequestStatusEnum;
import iclean.code.data.enumjava.WalletTypeEnum;
import iclean.code.data.repository.MoneyRequestRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.data.repository.WalletRepository;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.function.moneyrequest.service.MoneyRequestService;
import iclean.code.function.transaction.service.TransactionService;
import iclean.code.function.common.service.TwilioOTPService;
import iclean.code.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private TwilioOTPService twilioOTPService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${iclean.app.expired.time.minutes}")
    private Integer expiredTime;
    @Value("${iclean.app.message.deposit}")
    private String depositMessage;
    @Value("${iclean.app.message.withdraw}")
    private String withdrawMessage;
    @Override
    public ResponseEntity<ResponseObject> getMoneyRequests(String phoneNumber, Pageable pageable) {
        try {
            Sort order = Sort.by(Sort.Order.desc("requestDate"));
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), order);
            User user = findUserByPhoneNumber(phoneNumber);
            Page<MoneyRequest> moneyRequests = moneyRequestRepository.findAllByPhoneNumber(phoneNumber, pageable);
            List<GetMoneyRequestResponse> data = moneyRequests
                    .stream()
                    .map(moneyRequest -> modelMapper.map(moneyRequest, GetMoneyRequestResponse.class))
                    .collect(Collectors.toList());
            GetMoneyRequestUserResponse response = modelMapper.map(user, GetMoneyRequestUserResponse.class);
            PageResponseObject pageResponseObject = Utils.convertToPageResponse(moneyRequests, data);
            response.setData(pageResponseObject);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Money Request List",
                            response));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> createMoneyRequest(CreateMoneyRequestRequest request) {
        try {
            User user = findUserByPhoneNumber(request.getUserPhoneNumber());
            List<MoneyRequest> moneyRequests = moneyRequestRepository
                    .findAllByPhoneNumberAndPending(request.getUserPhoneNumber(), MoneyRequestStatusEnum.PENDING);
            for (MoneyRequest moneyRequest :
                    moneyRequests) {
                moneyRequest.setRequestStatus(MoneyRequestStatusEnum.CANCEL);
            }
            MoneyRequest moneyRequest = modelMapper.map(request, MoneyRequest.class);
            moneyRequest.setRequestType(MoneyRequestEnum.valueOf(request.getMoneyRequestType().toUpperCase()));
            moneyRequest.setRequestStatus(MoneyRequestStatusEnum.FAIL);
            moneyRequest.setUser(user);
            String token = twilioOTPService.sendAndGetOTPToken(user.getPhoneNumber());
            moneyRequest.setOtpToken(token);
            moneyRequest.setExpiredTime(Utils.getLocalDateTimeNow().plusMinutes(expiredTime));
            moneyRequest.setRequestStatus(MoneyRequestStatusEnum.PENDING);
            moneyRequestRepository.saveAll(moneyRequests);
            MoneyRequest moneyRequestUpdate = moneyRequestRepository.save(moneyRequest);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Money Request Successful",
                            moneyRequestUpdate.getRequestId()));
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
    public ResponseEntity<ResponseObject> validateMoneyRequest(ValidateMoneyRequest request) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestByPhoneNumber(request.getPhoneNumber());
            if (!moneyRequest.getRequestStatus().equals(MoneyRequestStatusEnum.PENDING)) {
                throw new BadRequestException("The request are expired");
            }
            ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest(request.getOtpToken(), moneyRequest.getOtpToken());
            if (twilioOTPService.validateOTP(validateOTPRequest)) {
                if (moneyRequest.getExpiredTime().isBefore(Utils.getLocalDateTimeNow()))
                    throw new BadRequestException("The OTP had expired");
                moneyRequest.setProcessDate(Utils.getLocalDateTimeNow());
                moneyRequest.setRequestStatus(MoneyRequestStatusEnum.SUCCESS);
            } else {
                throw new BadRequestException("Wrong OTP");
            }
            String note = moneyRequest.getRequestType().equals(MoneyRequestEnum.DEPOSIT) ?
                    MessageVariable.DEPOSIT_SUCCESSFUL
                    : MessageVariable.WITHDRAW_SUCCESSFUL;

            transactionService.createTransactionService(new TransactionRequest(moneyRequest.getBalance(), note,
                    moneyRequest.getUser().getUserId(), moneyRequest.getRequestType().name(), WalletTypeEnum.MONEY.name()));
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
            if (e instanceof BadRequestException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> resendOtp(String phoneNumber) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestByPhoneNumber(phoneNumber);
            if (!moneyRequest.getRequestStatus().equals(MoneyRequestStatusEnum.PENDING)) {
                throw new BadRequestException("The request are expired");
            }
            String token = twilioOTPService.sendAndGetOTPToken(moneyRequest.getUser().getPhoneNumber());
            moneyRequest.setOtpToken(token);
            moneyRequest.setExpiredTime(Utils.getLocalDateTimeNow().plusMinutes(expiredTime));
            moneyRequestRepository.save(moneyRequest);
            moneyRequestRepository.save(moneyRequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Resend OTP Request Successful",
                            null));
        } catch (Exception e) {
            if (e instanceof NotFoundException)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            if (e instanceof BadRequestException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    private MoneyRequest findMoneyRequestByPhoneNumber(String phone) {
        return moneyRequestRepository
                .findByPhoneNumber(phone, MoneyRequestStatusEnum.PENDING)
                .orElseThrow(() -> new NotFoundException("Money Request is not exist"));
    }

    private User findUserByPhoneNumber(String phoneNumber) {
        return userRepository
                .getUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(String.format("User Phone: %s is not exist", phoneNumber)));
    }
}
