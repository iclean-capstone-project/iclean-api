package iclean.code.data.repository;

import iclean.code.data.domain.ServiceUnit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceUnitRepository extends JpaRepository<ServiceUnit, Integer> {
    @Query("SELECT serunit FROM ServiceUnit serunit " +
            "WHERE serunit.service.serviceId = ?1 " +
            "AND serunit.isDeleted = FALSE ")
    List<ServiceUnit> findByService(Integer serviceId, Sort sort);

    @Query("SELECT serunit FROM ServiceUnit serunit " +
            "WHERE serunit.service.serviceId = ?1 " +
            "AND serunit.isDeleted = FALSE ")
    List<ServiceUnit> findByServiceActive(Integer serviceId);
}