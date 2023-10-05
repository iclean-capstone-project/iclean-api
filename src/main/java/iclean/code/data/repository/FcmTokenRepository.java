package iclean.code.data.repository;

import iclean.code.data.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Integer> {
    @Query("SELECT fcm FROM FcmToken fcm WHERE fcm.fcmToken LIKE ?1")
    Optional<FcmToken> findByToken(String fcmToken);
}
