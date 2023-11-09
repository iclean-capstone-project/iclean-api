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

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN b.bookingStatusHistories bs " +
            "WHERE b.renter.userId = ?1 " +
            "AND bs.bookingStatus = ?2 " +
            "AND bs.createAt = (SELECT MAX(bsh.createAt) FROM BookingStatusHistory bsh " +
            "WHERE bsh.statusHistoryId = bs.statusHistoryId)")
    Page<Booking> findBookingHistoryByUserId(Integer userId, BookingStatusEnum status, Pageable pageable);

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

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN b.bookingDetails bd " +
            "LEFT JOIN b.bookingStatusHistories bs " +
            "WHERE bd.bookingDetailId = ?1 " +
            "AND bs.bookingStatus = ?2 " +
            "AND bs.createAt = (SELECT MAX(bsh.createAt) FROM BookingStatusHistory bsh " +
            "WHERE bsh.statusHistoryId = bs.statusHistoryId)")
    Booking findBookingByBookingDetailAndStatus(Integer bookingDetailId, BookingStatusEnum bookingStatusEnum);

    @Query("SELECT e FROM Booking e WHERE DATE(e.orderDate) = ?1")
    List<Booking> getBookingByOrderDate(String orderDate);

    @Query("SELECT e FROM Booking e WHERE MONTH(e.orderDate) = CAST(?1 AS int) AND YEAR(e.orderDate) = YEAR(CURRENT_DATE)")
    List<Booking> getBookingByMoth(String orderDate);

    @Query("SELECT b FROM Booking b WHERE WEEK(b.orderDate) = WEEK(CURRENT_DATE)")
    List<Booking> getBookingInCurrentWeek();

    @Query("select sum(b.totalPriceActual) from Booking b")
    Double getSumOfIncome();


}