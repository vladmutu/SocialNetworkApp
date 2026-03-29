package org.example.socialnetwork.Persistence.Repository;

import org.example.socialnetwork.Persistence.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}

