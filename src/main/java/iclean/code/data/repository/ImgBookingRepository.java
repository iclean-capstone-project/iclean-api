package iclean.code.data.repository;

import iclean.code.data.domain.ImgBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgBookingRepository extends JpaRepository<ImgBooking, Integer> {
}
