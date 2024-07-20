package com.gitlab.repository;

import com.gitlab.model.Store;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StoreRepository extends ReadOnlyRepository<Store, Long> {
    @Override
    @NonNull
    @EntityGraph(value = "store")
    Optional<Store> findById(Long id);

    @Override
    @NonNull
    @EntityGraph(value = "store")
    @Query("SELECT s FROM Store s WHERE s.entityStatus = 'ACTIVE'")
    List<Store> findAll();

    @Override
    @NonNull
    @EntityGraph(value = "store")
    @Query("SELECT s FROM Store s WHERE s.entityStatus = 'ACTIVE'")
    Page<Store> findAll(Pageable pageable);

    @NonNull
    @Query(value = "SELECT managers_id FROM store_managers", nativeQuery = true)
    Set<Long> findDistinctByManagers();
}
