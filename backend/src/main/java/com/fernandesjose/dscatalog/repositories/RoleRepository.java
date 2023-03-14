package com.fernandesjose.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fernandesjose.dscatalog.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
