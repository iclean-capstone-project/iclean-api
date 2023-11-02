package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.enumjava.BookingStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {
    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr WHERE " +
            "sr.serviceRegistrationId = ?1")
    Page<BookingDetail> findByServiceRegistrationId(Integer serviceRegistrationId, Pageable pageable);

    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.booking booking " +
            "LEFT JOIN booking.bookingStatusHistories bsh " +
            "WHERE bookingDetail.serviceUnit.serviceUnitId = ?1 " +
            "AND bsh.bookingStatus = ?2 " +
            "AND booking.bookingStatusHistories.size = 1")
    Optional<BookingDetail> findByServiceUnitIdAndBookingStatus(Integer id, BookingStatusEnum bookingStatusEnum);

    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.booking booking " +
            "LEFT JOIN booking.bookingStatusHistories bsh " +
            "WHERE bookingDetail.bookingDetailId = ?1 " +
            "AND bsh.bookingStatus = ?2 " +
            "AND booking.bookingStatusHistories.size = 1")
    Optional<BookingDetail> findByBookingDetailIdAndBookingStatus(Integer id, BookingStatusEnum bookingStatusEnum);
}