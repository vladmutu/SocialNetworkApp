package org.example.socialnetwork.Persistence.Repository;

import org.example.socialnetwork.Persistence.Entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
}

