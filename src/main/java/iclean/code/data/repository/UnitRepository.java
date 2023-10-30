package iclean.code.data.repository;

import iclean.code.data.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
    @Query("SELECT t FROM Unit t WHERE t.isDeleted = FALSE")
    List<Unit> findAllActive();
}
