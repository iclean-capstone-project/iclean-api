package iclean.code.data.repository;

import iclean.code.data.domain.BookingStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Integer> {
}
