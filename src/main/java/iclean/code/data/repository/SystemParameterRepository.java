package iclean.code.data.repository;

import iclean.code.data.domain.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Integer> {
    @Query("SELECT sts FROM SystemParameter sts WHERE sts.parameterField LIKE ?1")
    SystemParameter findSystemParameterByParameterField(String fieldName);
}
