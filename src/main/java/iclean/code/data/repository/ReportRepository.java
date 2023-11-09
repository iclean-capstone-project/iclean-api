package iclean.code.data.repository;

import iclean.code.data.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    Report findReportByBookingBookingId(int bookingId);

    @Query("SELECT report FROM Report report " +
            "WHERE report.booking.manager.userId = ?1 " +
            "AND report.booking.renter.fullName = ?2 " +
            "ORDER BY report.reportStatus ASC, " +
            "report.createAt ASC ")
    Page<Report> findReportsAsManager(Integer managerId, String renterName, Pageable pageable);

    @Query("SELECT report FROM Report report WHERE report.booking.renter.fullName = ?1")
    Page<Report> findAllReportByRenterName(String renterName, Pageable pageable);
}
