package com.gitlab.repository;
import com.gitlab.model.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @NonNull
    @EntityGraph(value = "userWithSets", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT u FROM User u WHERE u.entityStatus = 'ACTIVE' order by u.id asc")
    List<User> findAll();

    @Override
    @NonNull
    @Query("SELECT u FROM User u WHERE u.entityStatus = 'ACTIVE' order by u.id asc")
    Page<User> findAll(Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(value = "userWithSets", type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findById(@NonNull Long id);

    Optional<User> findByUsername(@NonNull String username);
}