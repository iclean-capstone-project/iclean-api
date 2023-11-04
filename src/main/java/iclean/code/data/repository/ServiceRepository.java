package iclean.code.data.repository;

import iclean.code.data.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    @Query("SELECT t FROM Service t WHERE t.isDeleted = FALSE AND size(t.serviceUnits) > 0")
    List<Service> findAllActive();
}
