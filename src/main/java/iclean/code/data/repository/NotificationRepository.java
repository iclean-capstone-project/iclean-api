package iclean.code.data.repository;

import iclean.code.data.domain.MoneyPoint;
import iclean.code.data.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findNotificationByUserUserId(int userId);

    @Query("SELECT notification FROM Notification notification WHERE notification.user.userId = ?1")
    Page<Notification> findByUserIdPageable (Integer userId, Pageable pageable);

    @Query("SELECT notification FROM Notification notification")
    Page<Notification> findAll (Pageable pageable);
}
