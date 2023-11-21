package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetailStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingDetailStatusHistoryRepository extends JpaRepository<BookingDetailStatusHistory, Integer> {

    @Query(nativeQuery = true, value = "SELECT * " +
            "FROM booking_status_history bookingH " +
            "WHERE bookingH.booking_id = :bookingId " +
            "ORDER BY bookingH.create_at DESC " +
            "LIMIT 1")
    BookingDetailStatusHistory findTheLatestBookingStatusByBookingId(Integer bookingId);

    @Query("SELECT bdsh FROM BookingDetailStatusHistory bdsh WHERE " +
            "bdsh.bookingDetail.booking.bookingId = ?1 ")
    List<BookingDetailStatusHistory> findByBookingId(Integer bookingId);
}
