package iclean.code.data.repository;

import iclean.code.data.domain.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Integer> {
}
