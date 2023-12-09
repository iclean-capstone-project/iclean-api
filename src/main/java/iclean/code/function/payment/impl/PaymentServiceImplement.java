package iclean.code.function.payment.impl;

import iclean.code.config.MessageVariable;
import iclean.code.config.VnPayConfig;
import iclean.code.data.domain.Transaction;
import iclean.code.data.domain.User;
import iclean.code.data.domain.Wallet;
import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.response.authen.UserPrinciple;
import iclean.code.data.enumjava.TransactionStatusEnum;
import iclean.code.data.enumjava.TransactionTypeEnum;
import iclean.code.data.enumjava.WalletTypeEnum;
import iclean.code.data.repository.TransactionRepository;
import iclean.code.data.repository.UserRepository;
import iclean.code.data.repository.WalletRepository;
import iclean.code.exception.NotFoundException;
import iclean.code.function.payment.PaymentService;
import iclean.code.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PaymentServiceImplement implements PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<ResponseObject> createPayment(Long amount) {
        try {
            String orderType = "other";
            String bankCode = "NCB";

            String vnp_TxnRef = VnPayConfig.getRandomNumber(8);
            String vnp_IpAddr = "127.0.0.1";

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
            vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
            vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
            vnp_Params.put("vnp_CurrCode", "VND");

            vnp_Params.put("vnp_BankCode", bankCode);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            int userId = userPrinciple.getId();

            vnp_Params.put("vnp_OrderInfo", String.valueOf(userId));
            vnp_Params.put("vnp_OrderType", orderType);

            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(HttpStatus.OK.toString(),
                            "Payment URL",
                            paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                            "Something wrong occur!", null));
        }
    }

    @Override
    public ResponseEntity<ResponseObject> paymentReturn(HttpServletRequest request) {
        try {
            Map fields = new HashMap();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII);
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            String signValue = VnPayConfig.hashAllFields(fields);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            if (signValue.equals(request.getParameter("vnp_SecureHash"))) {
                if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                    LocalDateTime paymentDateTime = LocalDateTime.parse(request.getParameter("vnp_PayDate"), formatter);
                    int userId = Integer.parseInt(request.getParameter("vnp_OrderInfo"));
                    double balance = Double.parseDouble(request.getParameter("vnp_Amount")) / 100;
                    User user = findUserById(userId);

                    Wallet wallet = walletRepository.getWalletByUserIdAndType(userId,
                            WalletTypeEnum.valueOf(WalletTypeEnum.MONEY.name()));
                    if (Objects.isNull(wallet)) {
                        wallet = new Wallet();
                        wallet.setUser(user);
                        wallet.setBalance(balance);
                        wallet.setWalletTypeEnum(WalletTypeEnum.MONEY);
                    }
                    wallet.setBalance(wallet.getBalance() + balance);
                    wallet.setUpdateAt(Utils.getLocalDateTimeNow());
                    Wallet walletUpdate = walletRepository.save(wallet);

                    Transaction transaction = new Transaction();
                    transaction.setAmount(balance);
                    transaction.setCreateAt(paymentDateTime);
                    transaction.setTransactionStatusEnum(TransactionStatusEnum.SUCCESS);
                    transaction.setTransactionTypeEnum(TransactionTypeEnum.DEPOSIT);
                    transaction.setWallet(walletUpdate);
                    transaction.setNote(MessageVariable.DEPOSIT_SUCCESSFUL);
                    transactionRepository.save(transaction);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(HttpStatus.OK.toString(),
                                    "Payment handled successfully",
                                    transaction));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                    "Something wrong occur!", null));
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                                "Something wrong occur!", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(),
                            "Something wrong occur!", null));
        }
    }

    private User findUserById(Integer id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User ID: %s is not exist", id)));
    }
}
