package org.example.socialnetwork.Repository;

import org.example.socialnetwork.Domain.Entity;
import org.example.socialnetwork.Domain.Friendship;
import org.example.socialnetwork.Utils.Pageable.Page;
import org.example.socialnetwork.Utils.Pageable.Pageable;

public interface FriendshipPageRepository<L extends Number, F extends Entity<Long>> extends PagingRepository<Long, Friendship> {
    Page<F> findAllOnPage(Pageable pageable, L id);
}
