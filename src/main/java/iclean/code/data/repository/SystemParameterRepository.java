package iclean.code.data.repository;

import iclean.code.data.domain.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Integer> {
    SystemParameter findSystemParameterByParameterField(String fieldName);
}
