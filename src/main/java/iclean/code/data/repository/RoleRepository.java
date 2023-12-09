package iclean.code.data.repository;

import iclean.code.data.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT role FROM Role role WHERE role.title LIKE ?1")
    Role findByTitle(String title);
}
