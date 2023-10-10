package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Booking findBookingByBookingId(int bookingId);

    @Query("SELECT b FROM Booking b WHERE b.manager = null")
    List<Booking> findAllWithNoManager();

    @Query("SELECT b FROM Booking b WHERE b.manager.userId =?3 " +
            "AND (b.renter.fullName LIKE ?1 " +
            "OR b.staff.fullName LIKE ?1) " +
            "AND ?2 IN (SELECT s FROM b.bookingStatusHistories s WHERE " +
            "s.bookingStatus.titleStatus LIKE ?3)")
    Page<Booking> findAllByManagerIdAndStatusAndSearchByName(String search,
                                                             String status,
                                                             Integer managerId,
                                                             Pageable pageable);
}
