package iclean.code.data.repository;

import iclean.code.data.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {
    @Query("SELECT fcm FROM DeviceToken fcm WHERE fcm.fcmToken LIKE ?1")
    Optional<DeviceToken> findByToken(String fcmToken);

    @Query("SELECT fcm FROM DeviceToken fcm WHERE fcm.user.userId = ?1")
    List<DeviceToken> findByUserId(Integer userId);
}
