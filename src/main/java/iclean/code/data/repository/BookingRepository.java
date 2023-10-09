package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Booking findBookingByBookingId(int bookingId);

    @Query("SELECT booking FROM Booking booking WHERE booking.renter.userId = ?1")
    Page<Booking> findByRenterId(Integer userId, Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.staff.userId = ?1")
    Page<Booking> findByStaffId(Integer userId, Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.staff.userId = ?2 AND booking.bookingId = ?1")
    Page<Booking> findBookingByBookingId(Integer bookingId, Integer userId, Pageable pageable);
}
