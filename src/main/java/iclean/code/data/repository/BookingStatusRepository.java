package iclean.code.data.repository;

import iclean.code.data.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingStatusRepository extends JpaRepository<BookingStatus, Integer> {
    BookingStatus findBookingStatusByBookingStatusId(int id);

}
