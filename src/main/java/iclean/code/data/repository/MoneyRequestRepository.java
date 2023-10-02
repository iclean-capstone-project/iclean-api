package iclean.code.data.repository;

import iclean.code.data.domain.MoneyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Integer> {
}
