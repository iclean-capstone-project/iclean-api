package iclean.code.data.repository;

import iclean.code.data.domain.Transaction;
import iclean.code.data.enumjava.TransactionTypeEnum;
import iclean.code.data.enumjava.WalletTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT trans FROM Transaction trans WHERE trans.wallet.user.userId = ?1 " +
            "AND trans.wallet.walletTypeEnum = ?2")
    Page<Transaction> findByUserUserId(Integer userId, WalletTypeEnum walletTypeEnum, Pageable pageable);

    @Query("SELECT trans FROM Transaction trans WHERE trans.wallet.user.userId = ?4 " +
            "AND trans.wallet.walletTypeEnum = ?2 " +
            "AND trans.transactionTypeEnum = ?3 " +
            "AND trans.booking.bookingId = ?1 ")
    Transaction findByBookingIdAndWalletTypeAndTransactionTypeAndUserId(Integer bookingId, WalletTypeEnum walletTypeEnum, TransactionTypeEnum transactionTypeEnum, Integer renterId);
}
