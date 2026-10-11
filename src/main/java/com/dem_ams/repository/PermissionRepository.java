package com.dem_ams.repository;

import com.dem_ams.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
