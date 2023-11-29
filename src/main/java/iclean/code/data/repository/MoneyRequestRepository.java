package iclean.code.data.repository;

import iclean.code.data.domain.MoneyRequest;
import iclean.code.data.enumjava.MoneyRequestStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Integer> {
    @Query("SELECT moneyRequest FROM MoneyRequest moneyRequest " +
            "WHERE moneyRequest.user.phoneNumber = ?1")
    Page<MoneyRequest> findAllByPhoneNumber(String phoneNumber, Pageable pageable);

    @Query("SELECT moneyRequest FROM MoneyRequest moneyRequest " +
            "WHERE moneyRequest.user.phoneNumber = ?1 " +
            "AND moneyRequest.requestStatus = ?2")
    Optional<MoneyRequest> findByPhoneNumber(String phone, MoneyRequestStatusEnum moneyRequestStatusEnum);

    @Query("SELECT moneyRequest FROM MoneyRequest moneyRequest " +
            " WHERE moneyRequest.user.phoneNumber = ?1 " +
            " AND moneyRequest.requestStatus = ?2")
    List<MoneyRequest> findAllByPhoneNumberAndPending(String userPhoneNumber, MoneyRequestStatusEnum moneyRequestStatusEnum);
}
