package iclean.code.data.repository;

import iclean.code.data.domain.Transaction;
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
}
