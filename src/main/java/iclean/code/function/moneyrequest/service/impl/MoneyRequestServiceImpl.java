package iclean.code.function.moneyrequest.service.impl;

import iclean.code.data.domain.MoneyRequest;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequestDTO;
import iclean.code.data.dto.request.moneyrequest.ValidateMoneyRequestDTO;
import iclean.code.data.dto.request.security.ValidateOTPRequest;
import iclean.code.data.dto.request.transaction.TransactionRequestDto;
import iclean.code.data.dto.response.PageResponseObject;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestResponseDTO;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestUserDto;
import iclean.code.data.enumjava.MoneyRequestEnum;
import iclean.code.data.enumjava.MoneyRequestStatusEnum;
import iclean.code.data.enumjava.WalletType;
import iclean.code.data.repository.MoneyRequestRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.data.repository.WalletRepository;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.function.moneyrequest.service.MoneyRequestService;
import iclean.code.function.transaction.service.TransactionService;
import iclean.code.service.TwilioOTPService;
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
            List<GetMoneyRequestResponseDTO> data = moneyRequests
                    .stream()
                    .map(moneyRequest -> modelMapper.map(moneyRequest, GetMoneyRequestResponseDTO.class))
                    .collect(Collectors.toList());
            GetMoneyRequestUserDto response = modelMapper.map(user, GetMoneyRequestUserDto.class);
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
            User user = findUserByPhoneNumber(request.getUserPhoneNumber());
            MoneyRequest moneyRequest = modelMapper.map(request, MoneyRequest.class);
            moneyRequest.setRequestType(MoneyRequestEnum.valueOf(request.getMoneyRequestType().toUpperCase()));
            moneyRequest.setRequestStatus(MoneyRequestStatusEnum.FAIL);
            moneyRequest.setUser(user);
            String token = twilioOTPService.sendAndGetOTPToken(user.getPhoneNumber());
            moneyRequest.setOtpToken(token);
            moneyRequest.setExpiredTime(Utils.getDateTimeNow().plusMinutes(expiredTime));
            moneyRequest.setRequestStatus(MoneyRequestStatusEnum.PENDING);
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
    public ResponseEntity<ResponseObject> validateMoneyRequest(ValidateMoneyRequestDTO request) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestById(request.getRequestId());
            if (!moneyRequest.getRequestStatus().equals(MoneyRequestStatusEnum.PENDING)) {
                throw new BadRequestException("The request are expired");
            }
            ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest(request.getOtpToken(), moneyRequest.getOtpToken());
            if (twilioOTPService.validateOTP(validateOTPRequest)) {
                if (moneyRequest.getExpiredTime().isBefore(Utils.getDateTimeNow()))
                    throw new BadRequestException("The OTP had expired");
                moneyRequest.setProcessDate(Utils.getDateTimeNow());
                moneyRequest.setRequestStatus(MoneyRequestStatusEnum.SUCCESS);
            } else {
                throw new BadRequestException("Wrong OTP");
            }
            String note = moneyRequest.getRequestType().equals(MoneyRequestEnum.DEPOSIT) ?
                    String.format(depositMessage, " + " + moneyRequest.getBalance().longValue())
                    : String.format(withdrawMessage, " - " + moneyRequest.getBalance().longValue());

            transactionService.createTransactionService(new TransactionRequestDto(moneyRequest.getBalance(), note,
                    moneyRequest.getUser().getUserId(), moneyRequest.getRequestType().name(), WalletType.MONEY.name()));
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
    public ResponseEntity<ResponseObject> resendOtp(Integer id) {
        try {
            MoneyRequest moneyRequest = findMoneyRequestById(id);
            if (!moneyRequest.getRequestStatus().equals(MoneyRequestStatusEnum.PENDING)) {
                throw new BadRequestException("The request are expired");
            }
            String token = twilioOTPService.sendAndGetOTPToken(moneyRequest.getUser().getPhoneNumber());
            moneyRequest.setOtpToken(token);
            moneyRequest.setExpiredTime(Utils.getDateTimeNow().plusMinutes(expiredTime));
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

    private MoneyRequest findMoneyRequestById(Integer id) {
        return moneyRequestRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Money Request ID: %s is not exist", id)));
    }

    private User findUserByPhoneNumber(String phoneNumber) {
        return userRepository
                .getUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException(String.format("User Phone: %s is not exist", phoneNumber)));
    }
}
