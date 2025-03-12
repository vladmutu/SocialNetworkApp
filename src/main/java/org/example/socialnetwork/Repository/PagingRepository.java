package org.example.socialnetwork.Repository;

import org.example.socialnetwork.Domain.Entity;
import org.example.socialnetwork.Utils.Pageable.Page;
import org.example.socialnetwork.Utils.Pageable.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID,E> {
    Page<E> findAllOnPage(Pageable pageable, ID id);
}
