package com.dem_ams.repository;

import com.dem_ams.domain.model.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String name);

    boolean existsByUsername(String username);
}
