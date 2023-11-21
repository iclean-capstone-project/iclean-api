package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.response.feedback.PointFeedbackOfHelper;
import iclean.code.data.dto.response.helperinformation.GetPriorityResponse;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import iclean.code.data.enumjava.BookingStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
            "WHERE bookingDetail.serviceUnit.service.serviceId = ?1 " +
            "AND booking.bookingStatus = ?2 ")
    Optional<BookingDetail> findByServiceIdAndBookingStatus(Integer id, BookingStatusEnum bookingStatusEnum);

    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "WHERE bookingDetail.bookingDetailId = ?1 " +
            "AND bookingDetail.bookingDetailStatus = ?2 ")
    Optional<BookingDetail> findByBookingDetailIdAndBookingStatus(Integer id, BookingDetailStatusEnum bookingDetailStatusEnum);

    @Query("SELECT NEW iclean.code.data.dto.response.feedback.PointFeedbackOfHelper(AVG(bd.rate), " +
            "COUNT(bd.feedback)) FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId = ?1 " +
            "AND bd.serviceUnit.service.serviceId = ?2")
    PointFeedbackOfHelper findPointByHelperId(Integer userId, Integer serviceId);

    List<BookingDetail> findBookingDetailByBookingBookingId (Integer bookingId);
    @Query("SELECT bd FROM BookingDetail bd " +
            "LEFT JOIN bd.booking b " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "WHERE bd.bookingDetailStatus = ?1 " +
            "AND NOT EXISTS (SELECT 1 FROM bdh.serviceRegistration.helperInformation.user u WHERE u.userId = ?2) " +
            "AND NOT EXISTS (SELECT 1 FROM bd.booking b WHERE b.renter.userId = ?2) ")
    List<BookingDetail> findBookingDetailByStatusAndNoUserIdNoEmployee(BookingDetailStatusEnum bookingStatusEnum, Integer userId);

    @Query("SELECT bd FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "WHERE bdh.serviceRegistration.helperInformation.user.userId = ?1 " +
            "AND bd.bookingDetailStatus != ?2 ")
    List<BookingDetail> findByHelperIdAndBookingStatus(Integer helper, BookingDetailStatusEnum bookingDetailStatusEnum);

    @Query("SELECT bd FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr " +
            "LEFT JOIN sr.helperInformation hi " +
            "LEFT JOIN hi.user u " +
            "WHERE u.userId = ?1 " +
            "AND bd.bookingDetailStatus IN ?2 " +
            "AND bd.bookingDetailStatus != ?3")
    Page<BookingDetail> findByHelperId(Integer userId, List<BookingDetailStatusEnum> bookingDetailStatusEnums, BookingDetailStatusEnum noBookingStatusEnum, Pageable pageable);

    @Query("SELECT bd FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr " +
            "LEFT JOIN sr.helperInformation hi " +
            "LEFT JOIN hi.user u " +
            "WHERE u.userId = ?1 " +
            "AND bd.bookingDetailStatus != ?2")
    Page<BookingDetail> findByHelperId(Integer userId, BookingDetailStatusEnum noStatus, Pageable pageable);
    @Query("SELECT bd FROM BookingDetail bd WHERE " +
            " bd.booking.renter.userId = ?1 " +
            "AND bd.bookingDetailStatus != ?2 ")
    Page<BookingDetail> findByRenterId(Integer userId, BookingDetailStatusEnum noBookingStatusEnum, Pageable pageable);

    @Query("SELECT bd FROM BookingDetail bd WHERE bd.booking.renter.userId = ?1 " +
            "AND bd.bookingDetailStatus IN ?2 " +
            "AND bd.bookingDetailStatus != ?3")
    Page<BookingDetail> findByRenterId(Integer userId, List<BookingDetailStatusEnum> bookingDetailStatusEnums, BookingDetailStatusEnum noBookingStatusEnum, Pageable pageable);

    @Query("SELECT bd FROM BookingDetail bd WHERE bd.bookingDetailStatus IN ?1 AND bd.bookingDetailStatus != ?2")
    Page<BookingDetail> findAllByBookingStatus(List<BookingDetailStatusEnum> bookingDetailStatusEnums, BookingDetailStatusEnum noBookingStatusEnum, Pageable pageable);

    @Query("SELECT bd FROM BookingDetail bd " +
            "WHERE bd.bookingDetailStatus != ?1")
    Page<BookingDetail> findAllBooking(BookingDetailStatusEnum bookingStatusEnum, Pageable pageable);

    @Query("SELECT bd FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bdh " +
            "WHERE bd.bookingDetailStatus = ?1 " +
            "AND bd.workDate = ?2 " +
            "AND bdh.bookingDetailHelperStatus != ?3 ")
    List<BookingDetail> findAllByApprovedAndNoHelper(BookingDetailStatusEnum bookingDetailStatusEnum, LocalDate now,
                                                     BookingDetailHelperStatusEnum bookingDetailHelperStatusEnum);

    @Query("SELECT NEW iclean.code.data.dto.response.helperinformation.GetPriorityResponse(COUNT(bd.bookingDetailId), " +
            "AVG(bd.rate), hi.helperInformationId) " +
            "FROM BookingDetail bd " +
            "LEFT JOIN bd.bookingDetailHelpers bds " +
            "LEFT JOIN bds.serviceRegistration sr " +
            "LEFT JOIN  sr.helperInformation hi " +
            "WHERE bd.serviceUnit.service.serviceId = ?2 " +
            "AND bd.bookingDetailStatus = ?1 " +
            "AND bds.serviceRegistration.helperInformation.helperInformationId = ?3 ")
    GetPriorityResponse findPriority(BookingDetailStatusEnum bookingDetailStatusEnum, Integer serviceId, Integer helperInformationId);

    @Query("SELECT bd FROM BookingDetail bd WHERE bd.bookingDetailStatus = ?1 AND bd.priceHelper > 0")
    List<BookingDetail> findAllByBookingDetailStatus(BookingDetailStatusEnum bookingDetailStatusEnum);
}