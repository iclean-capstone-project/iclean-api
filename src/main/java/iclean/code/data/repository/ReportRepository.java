package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import iclean.code.data.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    Report findReportByBookingBookingId(int bookingId);

    @Query("SELECT report FROM Report report")
    Page<Report> findAllAsAdminOrManager(Pageable pageable);

    @Query("SELECT report FROM Report report WHERE report.booking.renter.userId = ?1")
    Page<Report> finAllReportAsRenter(Integer userId, Pageable pageable);

    @Query("SELECT report FROM Report report WHERE report.booking.staff.userId = ?1")
    Page<Report> finAllReportAsStaff(Integer userId, Pageable pageable);
}
