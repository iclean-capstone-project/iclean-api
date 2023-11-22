package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import iclean.code.data.dto.response.booking.GetSumOfBookingPerDay;
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

    @Query("SELECT booking FROM Booking booking " +
            "WHERE booking.bookingStatus != ?1")
    Page<Booking> findAllBooking(BookingStatusEnum bookingStatusEnum, Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE booking.renter.userId = ?1 " +
            "AND booking.bookingStatus IN ?2 " +
            "AND booking.bookingStatus != ?3")
    Page<Booking> findByRenterId(Integer userId, List<BookingStatusEnum> bookingStatusEnums, BookingStatusEnum noStatus, Pageable pageable);

    @Query("SELECT booking FROM Booking booking WHERE " +
            " booking.renter.userId = ?1 " +
            "AND booking.bookingStatus != ?2 ")
    Page<Booking> findByRenterId(Integer userId, BookingStatusEnum noStatus, Pageable pageable);

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN booking.bookingDetails bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr " +
            "LEFT JOIN sr.helperInformation hi " +
            "LEFT JOIN hi.user u " +
            "WHERE u.userId = ?1 " +
            "AND booking.bookingStatus IN ?2 " +
            "AND booking.bookingStatus != ?3")
    Page<Booking> findByHelperId(Integer userId, List<BookingStatusEnum> bookingStatusEnum, BookingStatusEnum noStatus, Pageable pageable);

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN booking.bookingDetails bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr " +
            "LEFT JOIN sr.helperInformation hi " +
            "LEFT JOIN hi.user u " +
            "WHERE u.userId = ?1 " +
            "AND booking.bookingStatus != ?2")
    Page<Booking> findByHelperId(Integer userId, BookingStatusEnum noStatus, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.renter.userId = ?1 " +
            "AND b.bookingStatus = ?2 ")
    Booking findCartByRenterId(Integer userId, BookingStatusEnum statusEnum);

    @Query("SELECT booking FROM Booking booking " +
            "LEFT JOIN booking.bookingDetails bd " +
            "WHERE booking.manager.userId = ?1")
    Page<Booking> findByManagerId(Integer userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN b.bookingDetails bd " +
            "WHERE bd.bookingDetailId = ?1 " +
            "AND b.bookingStatus = ?2 ")
    Booking findBookingByBookingDetailAndStatus(Integer bookingDetailId, BookingStatusEnum bookingStatusEnum);

    @Query("SELECT e FROM Booking e WHERE DATE(e.orderDate) = ?1")
    List<Booking> getBookingByOrderDate(String orderDate);

    @Query("SELECT new iclean.code.data.dto.response.booking.GetSumOfBookingPerDay(date(b.orderDate), count(b), sum(b.totalPriceActual)) " +
            "FROM Booking b " +
            "WHERE MONTH(b.orderDate) = ?1 and Year(b.orderDate) = ?2 " +
            "GROUP BY  DATE(b.orderDate)")
    List<GetSumOfBookingPerDay> getGetSumOfBookings(Integer month, Integer year);

    @Query("SELECT e FROM Booking e WHERE MONTH(e.orderDate) = CAST(?1 AS int) AND YEAR(e.orderDate) = YEAR(CURRENT_DATE)")
    List<Booking> getBookingByMoth(String orderDate);

    @Query("SELECT b FROM Booking b WHERE WEEK(b.orderDate) = WEEK(CURRENT_DATE)")
    List<Booking> getBookingInCurrentWeek();

    @Query("select sum(b.totalPriceActual) from Booking b")
    Double getSumOfIncome();

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus IN ?1 AND b.bookingStatus != ?2")
    Page<Booking> findAllByBookingStatus(List<BookingStatusEnum> bookingStatusEnums, BookingStatusEnum notStatus, Pageable pageable);
}