package iclean.code.data.repository;

import iclean.code.data.domain.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceImageRepository extends JpaRepository<ServiceImage, Integer> {
}
