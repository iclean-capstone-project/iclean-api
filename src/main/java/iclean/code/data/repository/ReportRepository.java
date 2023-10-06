package iclean.code.data.repository;

import iclean.code.data.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    Report findReportByBookingBookingId(int bookingId);
}
