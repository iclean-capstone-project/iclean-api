package iclean.code.data.repository;

import iclean.code.data.domain.ServiceUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceUnitRepository extends JpaRepository<ServiceUnit, Integer> {
}