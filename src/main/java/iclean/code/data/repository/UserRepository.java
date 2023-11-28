package iclean.code.data.repository;

import iclean.code.data.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT t FROM User t WHERE t.role.title not LIKE 'admin' ")
    List<User> findAllUserWithoutAdmin();

    @Query(value = "SELECT * FROM user WHERE WEEK(create_at) = WEEK(CURDATE())", nativeQuery = true)
    List<User> findNewUserInCurrentWeek();

    @Query("SELECT u FROM User u WHERE u.role.title IN ?1 " +
            "AND (u.phoneNumber LIKE ?2 OR u.fullName LIKE ?2)")
    Page<User> findAllByRole(List<String> role, String phoneName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.title IN ?1" +
            "AND u.isLocked = ?2 " +
            "AND (u.phoneNumber LIKE ?3 OR u.fullName LIKE ?3)")
    Page<User> findAllByRoleAndBanStatus(List<String> role, Boolean banStatus, String phoneName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.title IN ?1 " +
            "AND (u.isLocked = null OR u.isLocked = false ) " +
            "AND (u.phoneNumber LIKE ?2 OR u.fullName LIKE ?2)")
    Page<User> findAllByRoleAndNotBan(List<String> role, String phoneName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isLocked = ?1" +
            "AND (u.phoneNumber LIKE ?2 OR u.fullName LIKE ?2)")
    Page<User> findAllByBanStatus(boolean b, String phoneName, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (u.isLocked = null OR u.isLocked = false ) " +
            "AND (u.phoneNumber LIKE ?1 OR u.fullName LIKE ?1)")
    Page<User> findAllByNotBan(String phoneNumber, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            " u.phoneNumber LIKE ?1 OR u.fullName LIKE ?1")
    Page<User> findAllByPhoneName(String phoneName, Pageable pageable);
}
