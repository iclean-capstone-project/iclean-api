package iclean.code.data.repository;

import iclean.code.data.domain.ImgType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgTypeRepository extends JpaRepository<ImgType, Integer> {
}
