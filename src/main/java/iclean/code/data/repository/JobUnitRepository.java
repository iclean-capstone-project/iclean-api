package iclean.code.data.repository;

import iclean.code.data.domain.Job;
import iclean.code.data.domain.JobUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobUnitRepository extends JpaRepository<JobUnit, Integer> {
    @Query("SELECT t FROM JobUnit t WHERE t.isDelete = FALSE")
    List<JobUnit> findAllActive();
}
