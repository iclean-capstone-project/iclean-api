package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Booking findBookingByBookingId(int bookingId);

    @Query("SELECT b FROM Booking b WHERE b.manager = null")
    List<Booking> findAllWithNoManager();

    @Query("SELECT b FROM Booking b WHERE b.manager.userId =?3 " +
            "AND (b.renter.fullName LIKE ?1 " +
//            "OR b.employee.fullName LIKE ?1 " +
            "OR b.bookingDetails IN " +
            "(SELECT bd FROM BookingDetail bd WHERE bd.bookingDetailHelpers " +
            "IN (SELECT bdh FROM BookingDetailHelper bdh WHERE " +
            "bdh.serviceRegistration.helperInformation.fullName LIKE ?1))" +
            ") " +
            "AND ?2 IN (SELECT s FROM b.bookingStatusHistories s WHERE " +
            "s.bookingStatus = ?3)")
    Page<Booking> findAllByManagerIdAndStatusAndSearchByName(String search,
                                                             String status,
                                                             Integer managerId,
                                                             Pageable pageable);
    @Query("SELECT booking FROM Booking booking")
    Page<Booking> findAllBooking(Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.renter.userId = ?1")
    Page<Booking> findByRenterId(Integer userId, Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.bookingDetails IN " +
            "(SELECT bd FROM BookingDetail bd WHERE bd.bookingDetailHelpers " +
            "IN (SELECT bdh FROM BookingDetailHelper bdh WHERE " +
            "bdh.serviceRegistration.helperInformation.user.userId = ?1))")
    Page<Booking> findByStaffId(Integer userId, Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.bookingDetails IN " +
            "(SELECT bd FROM BookingDetail bd WHERE bd.bookingDetailHelpers " +
            "IN (SELECT bdh FROM BookingDetailHelper bdh WHERE " +
            "bdh.serviceRegistration.helperInformation.user.userId = ?1))" +
            "AND booking.bookingId = ?1")
    Page<Booking> findBookingByBookingId(Integer bookingId, Integer userId, Pageable pageable);

    @Query("SELECT b FROM Booking b  WHERE b.renter.userId = ?1")
    Page<Booking> findBookingHistoryByUserId(Integer userId, Pageable pageable);
}
