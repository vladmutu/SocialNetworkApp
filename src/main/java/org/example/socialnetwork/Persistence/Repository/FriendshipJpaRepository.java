package org.example.socialnetwork.Persistence.Repository;

import org.example.socialnetwork.Persistence.Entity.FriendshipEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, Long> {

    @Query("select f from FriendshipEntity f where (f.user1 = :id or f.user2 = :id) and f.pending = false")
    Page<FriendshipEntity> findAcceptedForUser(@Param("id") Long id, Pageable pageable);
}

