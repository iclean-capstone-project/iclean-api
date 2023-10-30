package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {
    @Query(value = "SELECT bookingDetail FROM BookingDetail bookingDetail " +
            "LEFT JOIN bookingDetail.bookingDetailHelpers bdh " +
            "LEFT JOIN bdh.serviceRegistration sr WHERE " +
            "sr.serviceRegistrationId = ?1")
    Page<BookingDetail> findByServiceRegistrationId(Integer serviceRegistrationId, Pageable pageable);
}