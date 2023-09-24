package iclean.code.data.repository;

import iclean.code.data.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    Booking findBookingByBookingId(int bookingId);
}
