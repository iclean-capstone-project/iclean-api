package iclean.code.data.repository;

import iclean.code.data.domain.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {
    @Query("SELECT schedule FROM WorkSchedule schedule " +
            "WHERE schedule.helperInformation.user.userId = ?1")
    List<WorkSchedule> findAllByUserId(int userId);
}
