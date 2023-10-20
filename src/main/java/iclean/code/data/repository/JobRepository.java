package iclean.code.data.repository;

import iclean.code.data.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    @Query("SELECT t FROM Job t WHERE t.isDelete = FALSE AND size(t.jobUnits) > 0")
    List<Job> findAllActive();

    Job findByJobId(int jobId);
}
