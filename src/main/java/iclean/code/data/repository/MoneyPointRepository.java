package iclean.code.data.repository;

import iclean.code.data.domain.MoneyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoneyPointRepository extends JpaRepository<MoneyPoint, Integer> {
    Optional<MoneyPoint> findMoneyPointByUserUserId(int userId);

}
