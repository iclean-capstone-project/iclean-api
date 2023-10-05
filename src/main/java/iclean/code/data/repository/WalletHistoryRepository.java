package iclean.code.data.repository;

import iclean.code.data.domain.WalletHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Integer> {
    @Query("SELECT wallet FROM WalletHistory wallet WHERE wallet.user.userId = ?1")
    List<WalletHistory> findByUserUserId(Integer userId);
}
