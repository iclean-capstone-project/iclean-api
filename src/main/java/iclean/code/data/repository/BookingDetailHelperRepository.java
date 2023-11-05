package iclean.code.data.repository;

import iclean.code.data.domain.BookingDetailHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailHelperRepository extends JpaRepository<BookingDetailHelper, Integer> {
    @Query(value = "SELECT hi.helper_information_id, hi.full_name, COUNT(*) AS count " +
            "FROM booking_detail_helper dhh " +
            "LEFT JOIN service_registration sr ON sr.service_registration_id = dhh.service_registration_id " +
            "LEFT JOIN helper_information hi ON hi.helper_information_id = sr.helper_information_id " +
            "WHERE MONTH(dhh.create_at) = MONTH(current_date) " +
            "GROUP BY hi.helper_information_id, hi.full_name " +
            "ORDER BY count DESC", nativeQuery = true)
    List<Object[]> findTopEmployeesInMonth();

    @Query(value = "SELECT hi.helper_information_id, hi.full_name, COUNT(*) AS count " +
            "FROM booking_detail_helper dhh " +
            "LEFT JOIN service_registration sr ON sr.service_registration_id = dhh.service_registration_id " +
            "LEFT JOIN helper_information hi ON hi.helper_information_id = sr.helper_information_id " +
            "WHERE DAY(dhh.create_at) = DAY(current_date) " +
            "GROUP BY hi.helper_information_id, hi.full_name " +
            "ORDER BY count DESC", nativeQuery = true)
    List<Object[]> findTopEmployeesOnDay();
}