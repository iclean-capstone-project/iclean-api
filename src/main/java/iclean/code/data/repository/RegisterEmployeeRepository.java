package iclean.code.data.repository;

import iclean.code.data.domain.RegisterEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterEmployeeRepository extends JpaRepository<RegisterEmployee, Integer> {
}
