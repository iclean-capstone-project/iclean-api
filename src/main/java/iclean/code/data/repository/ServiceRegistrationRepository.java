package iclean.code.data.repository;

import iclean.code.data.domain.HelperInformation;
import iclean.code.data.domain.ServiceRegistration;
import iclean.code.data.enumjava.HelperStatusEnum;
import iclean.code.data.enumjava.ServiceHelperStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRegistrationRepository extends JpaRepository<ServiceRegistration, Integer> {
    @Query("SELECT sregister FROM ServiceRegistration sregister " +
            "WHERE sregister.service.serviceId = ?1 " +
            "AND sregister.helperInformation.user.userId = ?2 " +
            "AND sregister.serviceHelperStatus = ?3")
    ServiceRegistration findByServiceIdAndUserId(Integer serviceId, Integer userId, ServiceHelperStatusEnum statusEnum);
    @Query("SELECT sregister FROM ServiceRegistration sregister " +
            "WHERE sregister.service.serviceId = ?1 " +
            "AND sregister.helperInformation.user.userId = ?2 ")
    ServiceRegistration findByServiceIdAndUserId(Integer serviceId, Integer userId);

    @Query("SELECT sregister FROM ServiceRegistration sregister " +
            "WHERE sregister.helperInformation.user.userId = ?1 ")
    List<ServiceRegistration> findServiceRegistrationByUserId(Integer userId);
}
