package iclean.code.data.repository;

import iclean.code.data.domain.HelperInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterEmployeeRepository extends JpaRepository<HelperInformation, Integer> {
}
