package iclean.code.data.repository;

import iclean.code.data.domain.RejectReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectReasonRepository extends JpaRepository<RejectReason, Integer> {
}
