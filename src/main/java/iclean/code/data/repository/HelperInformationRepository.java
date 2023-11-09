package iclean.code.data.repository;

import iclean.code.data.domain.HelperInformation;
import iclean.code.data.enumjava.HelperStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelperInformationRepository extends JpaRepository<HelperInformation, Integer> {
    @Query("SELECT info FROM HelperInformation info " +
            "WHERE info.user.userId = ?1")
    HelperInformation findByUserId(Integer userId);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.helperStatus = ?1 ")
    Page<HelperInformation> findAllByStatus(HelperStatusEnum helperStatusEnum, Pageable pageable);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.managerId = ?1 " +
            "AND hi.helperStatus = ?2")
    Page<HelperInformation> findAllByStatus(Integer managerId, HelperStatusEnum helperStatusEnum, Pageable pageable);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.managerId = ?1 " +
            "ORDER BY hi.helperStatus ASC ")
    Page<HelperInformation> findAllAndOrderByStatus(Integer managerId, Pageable pageable);
    @Query("SELECT hi FROM HelperInformation hi " +
            "ORDER BY hi.helperStatus ASC ")
    Page<HelperInformation> findAllAndOrderByStatus(Pageable pageable);
}
