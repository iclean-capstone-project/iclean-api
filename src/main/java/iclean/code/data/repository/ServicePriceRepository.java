package iclean.code.data.repository;

import iclean.code.data.domain.ServicePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePriceRepository extends JpaRepository<ServicePrice, Integer>  {
    @Query("SELECT servicePrice FROM ServicePrice servicePrice WHERE " +
            "servicePrice.serviceUnit.serviceUnitId = ?1 " +
            "AND servicePrice.isDelete = FALSE " +
            "ORDER BY servicePrice.startTime ")
    List<ServicePrice> findByServiceUnitId(Integer serviceUnitId);
}