package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetailHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingEmployeeRepository extends JpaRepository<BookingDetailHelper, Integer> {
    @Query("SELECT bdh FROM BookingDetailHelper bdh WHERE bdh.serviceRegistration.helperInformation.user.userId = ?1")
    Optional<BookingDetailHelper> findTopByEmployeeUserIdOrderByBookingEmpIdDesc (Integer empId);
}
