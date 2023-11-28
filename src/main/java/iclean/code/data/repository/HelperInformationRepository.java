package iclean.code.data.repository;

import iclean.code.data.domain.HelperInformation;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.HelperStatusEnum;
import iclean.code.data.enumjava.ServiceHelperStatusEnum;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HelperInformationRepository extends JpaRepository<HelperInformation, Integer> {
    @Query("SELECT info FROM HelperInformation info " +
            "WHERE info.user.userId = ?1")
    HelperInformation findByUserId(Integer userId);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.helperStatus IN ?1 ")
    Page<HelperInformation> findAllByStatus(List<HelperStatusEnum> helperStatusEnums, Pageable pageable);

    @Query("SELECT count(*) FROM HelperInformation hi " +
            "WHERE hi.meetingDateTime >= ?1 AND hi.meetingDateTime < ?2 AND hi.helperStatus = ?3")
    Integer findAllByMeetingDatetime(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            HelperStatusEnum statusEnum);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.meetingDateTime = (SELECT max(hi2.meetingDateTime) FROM HelperInformation hi2 WHERE hi2.helperStatus = ?1)")
    List<HelperInformation> findMaxByMeetingDateTimeAndHelperStatus(HelperStatusEnum helperStatusEnum);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.managerId = ?1 " +
            "AND hi.helperStatus IN ?2")
    Page<HelperInformation> findAllByStatus(Integer managerId, List<HelperStatusEnum> helperStatusEnums, Pageable pageable);
    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.managerId = ?1 " +
            "ORDER BY hi.helperStatus ASC ")
    Page<HelperInformation> findAllAndOrderByStatus(Integer managerId, Pageable pageable);
    @Query("SELECT hi FROM HelperInformation hi " +
            "ORDER BY hi.helperStatus ASC ")
    Page<HelperInformation> findAllAndOrderByStatus(Pageable pageable);

    @Query("SELECT hi FROM HelperInformation hi " +
            "LEFT JOIN hi.workSchedules ws " +
            "LEFT JOIN hi.serviceRegistrations sr " +
            "LEFT JOIN sr.bookingDetailHelpers bds " +
            "WHERE ws.startTime >= ?1 " +
            "AND ws.endTime <= ?2 " +
            "AND ws.dayOfWeek = ?3 " +
            "AND sr.service.serviceId = ?4 " +
            "AND sr.serviceHelperStatus = ?5 " +
            "AND NOT EXISTS (SELECT 1 FROM bds WHERE bds.bookingDetailHelperStatus = ?6) ")
    List<HelperInformation> findAllByWorkScheduleStartEndAndServiceId(LocalDateTime startDateTime, LocalDateTime endDateTime, DayOfWeek dayOfWeek, Integer serviceId,
                                                                      ServiceHelperStatusEnum serviceHelperStatusEnum, BookingDetailHelperStatusEnum bookingDetailHelperStatusEnum);

    @Query("SELECT hi FROM HelperInformation hi " +
            "WHERE hi.managerId = ?1 ")
    Page<HelperInformation> findAllByManagerId(Integer managerId, Pageable pageable);
}
