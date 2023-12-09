package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetailHelper;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDetailHelperRepository extends JpaRepository<BookingDetailHelper, Integer> {
    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.bookingDetail.bookingDetailId = ?1")
    List<BookingDetailHelper> findByBookingDetailId(Integer detailId);

    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.bookingDetail.bookingDetailId = ?1 " +
            "AND bdh.bookingDetailHelperStatus = ?2")
    List<BookingDetailHelper> findByBookingDetailIdAndActive(Integer detailId, BookingDetailHelperStatusEnum statusEnum);

    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.bookingDetail.bookingDetailId = ?1 " +
            "AND bdh.bookingDetailHelperStatus = ?2")
    BookingDetailHelper findByBookingDetailIdAndActiveLimit(Integer detailId, BookingDetailHelperStatusEnum statusEnum);
    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId = ?1 ")
    Optional<BookingDetailHelper> findByHelperId(Integer helperId);

    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId = ?1 " +
            "AND bdh.bookingDetailHelperStatus = ?2 " +
            "AND bdh.bookingDetail.bookingDetailStatus IN ?3 ")
    List<BookingDetailHelper> findByHelperIdAndIsActive(Integer helperId, BookingDetailHelperStatusEnum statusEnum, List<BookingDetailStatusEnum> bookingDetailStatusEnums);

    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId = ?2 " +
            "AND bdh.bookingDetail.bookingDetailId = ?1")
    Optional<BookingDetailHelper> findByBookingDetailIdAndHelperId(Integer bookingDetailId, Integer helperId);

    @Query(value = "SELECT hi.helper_information_id, hi.full_name, COUNT(*) AS count " +
            "FROM booking_detail_helper dhh " +
            "LEFT JOIN service_registration sr ON sr.service_registration_id = dhh.service_registration_id " +
            "LEFT JOIN helper_information hi ON hi.helper_information_id = sr.helper_information_id " +
            "WHERE MONTH(dhh.create_at) = MONTH(current_date) " +
            "GROUP BY hi.helper_information_id, hi.full_name " +
            "ORDER BY count DESC", nativeQuery = true)
    List<Object[]> findTopEmployeesInMonth();

    @Query(value = "SELECT hi.helper_information_id, hi.full_name, COUNT(*) AS count " +
            "FROM booking_detail_helper dhh " +
            "LEFT JOIN service_registration sr ON sr.service_registration_id = dhh.service_registration_id " +
            "LEFT JOIN helper_information hi ON hi.helper_information_id = sr.helper_information_id " +
            "WHERE DAY(dhh.create_at) = DAY(current_date) " +
            "GROUP BY hi.helper_information_id, hi.full_name " +
            "ORDER BY count DESC", nativeQuery = true)
    List<Object[]> findTopEmployeesOnDay();

    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId IN ?1 " +
            "AND bdh.bookingDetailHelperStatus = ?3 " +
            "AND bdh.bookingDetail.bookingDetailStatus IN ?2")
    List<BookingDetailHelper> findAlreadyWork(List<Integer> helperIds, List<BookingDetailStatusEnum> waiting, BookingDetailHelperStatusEnum bookingDetailHelperStatusEnum);
}