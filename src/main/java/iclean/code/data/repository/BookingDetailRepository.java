package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.response.feedback.PointFeedbackOfHelper;
import iclean.code.data.enumjava.BookingStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {
    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr WHERE " +
            "sr.helperInformation.user.userId = ?1 " +
            "AND bookingDetail.serviceUnit.service.serviceId = ?2 " +
            "ORDER BY bookingDetail.feedbackTime DESC ")
    Page<BookingDetail> findByServiceIdAndHelperId(Integer helperId, Integer serviceId, Pageable pageable);

    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.booking booking " +
            "LEFT JOIN booking.bookingStatusHistories bsh " +
            "WHERE bookingDetail.serviceUnit.serviceUnitId = ?1 " +
            "AND bsh.bookingStatus = ?2 " +
            "AND size(booking.bookingStatusHistories) = 1")
    Optional<BookingDetail> findByServiceUnitIdAndBookingStatus(Integer id, BookingStatusEnum bookingStatusEnum);

    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.booking booking " +
            "LEFT JOIN booking.bookingStatusHistories bsh " +
            "WHERE bookingDetail.bookingDetailId = ?1 " +
            "AND bsh.bookingStatus = ?2 " +
            "AND size(booking.bookingStatusHistories) = 1")
    Optional<BookingDetail> findByBookingDetailIdAndBookingStatus(Integer id, BookingStatusEnum bookingStatusEnum);

    @Query("SELECT bd FROM BookingDetail bd " +
            "LEFT JOIN bd.booking b " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "LEFT JOIN b.bookingStatusHistories bs " +
            "WHERE bs.bookingStatus = ?1 " +
            "AND bdh.serviceRegistration.helperInformation.user.userId != ?2 " +
            "AND bs.createAt = (SELECT MAX(bsh.createAt) FROM BookingStatusHistory bsh " +
            "WHERE bsh.statusHistoryId = bs.statusHistoryId)")
    List<BookingDetail> findBookingDetailByStatusAndNoUserId(BookingStatusEnum status, Integer helperId);

    @Query("SELECT NEW iclean.code.data.dto.response.feedback.PointFeedbackOfHelper(AVG(bd.rate), " +
            "COUNT(bd.feedback)) FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId = ?1 " +
            "AND bd.serviceUnit.service.serviceId = ?2")
    PointFeedbackOfHelper findPointByHelperId(Integer userId, Integer serviceId);

    List<BookingDetail> findBookingDetailByBookingBookingId (Integer bookingId);
}