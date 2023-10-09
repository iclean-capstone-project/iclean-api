package iclean.code.data.repository;

import iclean.code.data.domain.MoneyPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoneyPointRepository extends JpaRepository<MoneyPoint, Integer> {
    Optional<MoneyPoint> findMoneyPointByUserUserId(int userId);

    @Query("SELECT money FROM MoneyPoint money WHERE money.user.userId = ?1")
    Page<MoneyPoint> findByUserIdPageable (Integer userId, Pageable pageable);

    @Query("SELECT money FROM MoneyPoint money")
    Page<MoneyPoint> findAllMoneyPointAsAdminOrManager(Pageable pageable);
}
