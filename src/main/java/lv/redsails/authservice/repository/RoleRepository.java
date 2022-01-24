package lv.redsails.authservice.repository;

import lv.redsails.authservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository
        extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
