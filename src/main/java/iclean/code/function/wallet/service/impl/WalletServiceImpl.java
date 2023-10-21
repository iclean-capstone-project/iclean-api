package iclean.code.function.wallet.service.impl;

import iclean.code.data.domain.Wallet;
import iclean.code.data.domain.User;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.wallet.UpdateBalance;
import iclean.code.data.dto.response.wallet.CurrentBalanceDto;
import iclean.code.data.enumjava.WalletType;
import iclean.code.data.repository.WalletRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.wallet.service.WalletService;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<ResponseObject> getCurrentBalance(Integer userId, String walletTypeValue) {
        try {
            CurrentBalanceDto response = new CurrentBalanceDto();
            finUser(userId);
            WalletType walletType = WalletType.valueOf(walletTypeValue.toUpperCase());
            Wallet wallet = walletRepository.getWalletByUserIdAndType(userId, walletType);
//            CurrentBalanceDto response = modelMapper.map(wallet, CurrentBalanceDto.class);
            if (Objects.isNull(wallet)) {
                response.setCurrentBalance(0D);
                response.setWalletType(walletTypeValue);
            }
            response.setCurrentBalance(wallet.getBalance());
            response.setWalletType(walletType.name());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Current Balance of User",
                            response));
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString()
                                , e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> updateBalanceByUserId(int userId, UpdateBalance updateBalance) {
        try {
            Wallet walletForUpdate = mappingMoneyPointForUpdate(userId, updateBalance);
            walletRepository.save(walletForUpdate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString()
                            , "Update Balance of Wallet Successfully!", null));

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.toString(),
                                e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString()
                            , "Something wrong occur!", null));
        }
    }

    private Wallet mappingMoneyPointForUpdate(int userId, UpdateBalance updateBalance) {

        Wallet optionalWallet = walletRepository.getWalletByUserIdAndType(userId,
                WalletType.valueOf(updateBalance.getWalletType().toUpperCase()));

        if (Objects.isNull(optionalWallet)) {
            optionalWallet = new Wallet();
            optionalWallet.setUser(finUser(userId));
            optionalWallet.setWalletType(WalletType.valueOf(updateBalance.getWalletType().toUpperCase()));
        }
        optionalWallet.setBalance(Double.valueOf(updateBalance.getBalance()));
        optionalWallet.setUpdateAt(Utils.getDateTimeNow());
        return optionalWallet;
    }

    private User finUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not exist"));
    }
}
