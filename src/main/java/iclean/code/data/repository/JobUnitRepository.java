package iclean.code.data.repository;

import iclean.code.data.domain.JobUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobUnitRepository extends JpaRepository<JobUnit, Integer> {
}
