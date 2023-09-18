package iclean.code.data.repository;

import iclean.code.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT t FROM User t WHERE t.username = ?1")
    User findByUsername(String username);

    @Query("SELECT t.email FROM User t WHERE t.username = ?1")
    String findEmailByUsername(String username);

    @Query("SELECT t.username FROM User t WHERE t.email = ?1")
    String findUsernameByEmail(String email);

    @Query("SELECT t FROM User t WHERE t.email = ?1")
    User findByEmail(String email);
    @Query("SELECT t FROM User t WHERE t.phoneNumber = ?1")
    User findUserByPhoneNumber(String phoneNumber);
}
