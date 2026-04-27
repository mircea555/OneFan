package com.onefan.backend.repository;

import com.onefan.backend.model.Role;
import com.onefan.backend.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
