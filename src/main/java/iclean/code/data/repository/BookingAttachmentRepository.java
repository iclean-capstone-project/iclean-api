package iclean.code.data.repository;

import iclean.code.data.domain.ReportAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingAttachmentRepository extends JpaRepository<ReportAttachment, Integer> {
}
