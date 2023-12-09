package iclean.code.data.repository;

import iclean.code.data.domain.RejectionReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectionReasonRepository extends JpaRepository<RejectionReason, Integer> {
}
