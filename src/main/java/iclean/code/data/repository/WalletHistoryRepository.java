package iclean.code.data.repository;

import iclean.code.data.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletHistoryRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT trans FROM Transaction trans WHERE trans.user.userId = ?1")
    List<Transaction> findByUserUserId(Integer userId);
}
