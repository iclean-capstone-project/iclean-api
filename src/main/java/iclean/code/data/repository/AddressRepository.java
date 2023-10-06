package iclean.code.data.repository;

import iclean.code.data.domain.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("SELECT addr FROM Address addr WHERE addr.user.userId = ?1")
    Page<Address> findByUserId(Integer userId, Pageable pageable);

    @Query("SELECT addr FROM Address addr WHERE addr.user.userId = ?1 AND addr.isDefault = true")
    List<Address> findByUserIdAnAndIsDefault(Integer userId);
}
