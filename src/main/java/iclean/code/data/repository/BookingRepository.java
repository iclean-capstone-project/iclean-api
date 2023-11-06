package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import iclean.code.data.enumjava.BookingStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.manager = null")
    List<Booking> findAllWithNoManager();

    @Query("SELECT booking FROM Booking booking")
    Page<Booking> findAllBooking(Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.renter.userId = ?1")
    Page<Booking> findByRenterId(Integer userId, Pageable pageable);

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN booking.bookingDetails bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr " +
            "LEFT JOIN sr.helperInformation hi " +
            "LEFT JOIN hi.user u " +
            "WHERE u.userId = ?1")
    Page<Booking> findByHelperId(Integer userId, Pageable pageable);

    @Query("SELECT b FROM Booking b  WHERE b.renter.userId = ?1")
    Page<Booking> findBookingHistoryByUserId(Integer userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN b.bookingStatusHistories bds " +
            "WHERE b.renter.userId = ?1 " +
            "AND bds.bookingStatus = ?2 " +
            "AND size(b.bookingStatusHistories) <= 1")
    Booking findCartByRenterId(Integer userId, BookingStatusEnum statusEnum);

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN booking.bookingDetails bd " +
            "WHERE booking.manager.userId = ?1")
    Page<Booking> findByManagerId(Integer userId, Pageable pageable);
}