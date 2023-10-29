package iclean.code.data.repository;

import iclean.code.data.domain.MoneyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Integer> {
    @Query("SELECT moneyRequest FROM MoneyRequest moneyRequest " +
            "WHERE moneyRequest.user.phoneNumber = ?1")
    Page<MoneyRequest> findAllByPhoneNumber(String phoneNumber, Pageable pageable);
}
