package iclean.code.data.repository;

import iclean.code.data.domain.BookingStatusHistory;
import iclean.code.data.enumjava.BookingStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Integer> {

    @Query(nativeQuery = true, value = "SELECT * " +
            "FROM booking_status_history bookingH " +
            "WHERE bookingH.booking_id = :bookingId " +
            "ORDER BY bookingH.create_at DESC " +
            "LIMIT 1")
    BookingStatusHistory findTheLatestBookingStatusByBookingId(Integer bookingId);

    @Query("SELECT b FROM BookingStatusHistory b " +
            "WHERE b.bookingStatus = ?1 " +
            "AND DATEDIFF(CURRENT_TIMESTAMP, b.createAt) >= 3  AND DATEDIFF(CURRENT_TIMESTAMP, b.createAt) <= 4 " +
            "ORDER BY  b.createAt desc")
    List<BookingStatusHistory> findBookingStatusHistoryFinishedAfterThreeDays(BookingStatusEnum statusEnum);
}
