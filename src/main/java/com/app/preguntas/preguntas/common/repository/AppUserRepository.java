package com.app.preguntas.preguntas.common.repository;

import com.app.preguntas.preguntas.common.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
