package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetailStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingDetailStatusHistoryRepository extends JpaRepository<BookingDetailStatusHistory, Integer> {

    @Query("SELECT bdsh FROM BookingDetailStatusHistory bdsh WHERE " +
            "bdsh.bookingDetail.booking.bookingId = ?1 ")
    List<BookingDetailStatusHistory> findByBookingId(Integer bookingId);
}
