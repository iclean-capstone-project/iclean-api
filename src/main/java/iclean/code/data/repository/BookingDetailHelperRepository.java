package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetailHelper;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailHelperRepository extends JpaRepository<BookingDetailHelper, Integer> {
    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.bookingDetail.bookingDetailId = ?1")
    List<BookingDetailHelper> findByBookingDetailId(Integer detailId);

    @Query("SELECT bdh FROM BookingDetailHelper bdh " +
            "WHERE bdh.bookingDetail.bookingDetailId = ?1 " +
            "AND bdh.bookingDetailHelperStatus = ?2")
    List<BookingDetailHelper> findByBookingDetailIdAndActive(Integer detailId, BookingDetailHelperStatusEnum statusEnum);

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
            "LEFT JOIN bdh.bookingDetail bd " +
            "LEFT JOIN bd.booking b " +
            "LEFT JOIN b.bookingStatusHistories bsh " +
            "WHERE bsh.statusHistoryId = ?1 AND bd.bookingDetailStatusEnum = ?2")
    List<BookingDetailHelper> findBookingDetailHelperHaveFinishedStatus(Integer statusBookingHistoryId, BookingDetailStatusEnum bookingDetailStatusEnum);
}