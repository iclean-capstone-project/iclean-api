package iclean.code.function.transaction.service.impl;

import iclean.code.data.domain.User;
import iclean.code.data.domain.Transaction;
import iclean.code.data.domain.Wallet;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.transaction.TransactionRequestDto;
import iclean.code.data.dto.response.transaction.GetTransactionDetailResponseDto;
import iclean.code.data.dto.response.transaction.GetTransactionResponseDto;
import iclean.code.data.enumjava.Role;
import iclean.code.data.enumjava.TransactionStatus;
import iclean.code.data.enumjava.TransactionType;
import iclean.code.data.enumjava.WalletType;
import iclean.code.data.repository.UserRepository;
import iclean.code.data.repository.WalletHistoryRepository;
import iclean.code.data.repository.WalletRepository;
import iclean.code.exception.BadRequestException;
import iclean.code.exception.NotFoundException;
import iclean.code.exception.UserNotHavePermissionException;
import iclean.code.function.transaction.service.TransactionService;
import iclean.code.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private WalletHistoryRepository walletHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ResponseEntity<ResponseObject> getTransactions(Integer userId) {
        try {
            List<Transaction> transactions = walletHistoryRepository.findByUserUserId(userId);
            List<GetTransactionResponseDto> responses = transactions
                    .stream()
                    .map(transaction -> modelMapper.map(transaction, GetTransactionResponseDto.class))
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Wallet History List",
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
    public ResponseEntity<ResponseObject> getTransaction(Integer id,
                                                         Integer userId) {
        try {
            Transaction transaction = findWalletHistoryById(id);
            if (!Objects.equals(userId, transaction.getUser().getUserId()))
                throw new UserNotHavePermissionException();

            GetTransactionDetailResponseDto responses = modelMapper.map(transaction, GetTransactionDetailResponseDto.class);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Wallet History Information",
                            responses));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof UserNotHavePermissionException) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.toString(),
                                e.getMessage(),
                                null));
            }
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(),
                                null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Internal System Error",
                            null));
        }
    }

    private Transaction mappingForCreate(TransactionRequestDto request) {
        Transaction transaction = modelMapper.map(request, Transaction.class);
        transaction.setAmount(request.getBalance());
        transaction.setCreateAt(Utils.getDateTimeNow());
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionType(TransactionType.valueOf(request.getTransactionType().toUpperCase()));
        return transaction;
    }

    @Override
    public ResponseEntity<ResponseObject> createTransaction(TransactionRequestDto request) {
        try {
            Transaction transaction = mappingForCreate(request);
            User user = findUserById(request.getUserId());
            if (!Objects.equals(user.getRole().getTitle().toUpperCase(), Role.EMPLOYEE.name()) &&
                    !user.getRole().getTitle().toUpperCase().equals(Role.RENTER.name())) {
                throw new BadRequestException("This user cannot have this information");
            }
            transaction.setUser(user);
            Wallet wallet = walletRepository.getWalletByUserIdAndType(request.getUserId(),
                    WalletType.valueOf(request.getWalletType().toUpperCase()));
            if (Objects.isNull(wallet)) {
                wallet = new Wallet();
                wallet.setUser(user);
                wallet.setBalance(0D);
                wallet.setWalletType(WalletType.valueOf(request.getWalletType().toUpperCase()));
            }

            wallet.setUpdateAt(Utils.getDateTimeNow());
            TransactionType transactionType = TransactionType.valueOf(request.getTransactionType().toUpperCase());
            switch (transactionType) {
                case DEPOSIT:
                    wallet.setBalance(wallet.getBalance() + request.getBalance());
                    break;
                case TRANSFER:
                    break;
                case WITHDRAW:
                    if (wallet.getBalance() < request.getBalance()) {
                        throw new BadRequestException("The balance of user is less than the request balance");
                    }
                    wallet.setBalance(wallet.getBalance() - request.getBalance());
                    break;
            }
            walletRepository.save(wallet);
            walletHistoryRepository.save(transaction);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Create new Transaction Successful",
                            null));
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof BadRequestException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                                e.getMessage(),
                                null));
            }
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

    private User findUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }

    private Transaction findWalletHistoryById(Integer id) {
        return walletHistoryRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Wallet History ID: %s is not exist", id)));
    }
}
