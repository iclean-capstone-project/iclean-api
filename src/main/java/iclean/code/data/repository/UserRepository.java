package iclean.code.data.repository;

import iclean.code.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT t FROM User t WHERE t.username = ?1")
    User findByUsername(String username);
    @Query("SELECT t FROM User t WHERE t.email = ?1")
    User findByEmail(String email);
    User findByFacebookUid(String facebookUid);
    @Query("SELECT t FROM User t WHERE t.phoneNumber = ?1")
    User findUserByPhoneNumber(String phoneNumber);

    @Query("SELECT t FROM User t WHERE t.phoneNumber = ?1")
    Optional<User> getUserByPhoneNumber(String phoneNumber);

    User findByUserId(int id);

    @Query("SELECT t FROM User t WHERE t.role.title LIKE 'manager'")
    List<User> findAllManager();
}
