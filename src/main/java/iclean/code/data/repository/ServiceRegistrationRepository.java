package iclean.code.data.repository;

import iclean.code.data.domain.ServiceRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRegistrationRepository extends JpaRepository<ServiceRegistration, Integer> {

}
