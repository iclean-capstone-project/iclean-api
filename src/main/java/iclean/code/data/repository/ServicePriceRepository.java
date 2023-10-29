package iclean.code.data.repository;

import iclean.code.data.domain.ServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ServicePriceRepository extends JpaRepository<ServicePrice, Integer>  {
    @Query("SELECT servicePrice FROM ServicePrice servicePrice WHERE " +
            "servicePrice.startTime >= ?1 " +
            "AND servicePrice.endTime <= ?2")
    List<ServicePrice> findByStartTime(LocalTime startTime, LocalTime endTime);
}
