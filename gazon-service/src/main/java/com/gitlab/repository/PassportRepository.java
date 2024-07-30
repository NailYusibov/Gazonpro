package com.gitlab.repository;

import com.gitlab.model.Passport;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {

    @Override
    @NonNull
    @Query("SELECT p FROM Passport p WHERE p.entityStatus = 'ACTIVE' order by p.id asc")
    List<Passport> findAll();

    @Override
    @NonNull
    @Query("SELECT p FROM Passport p WHERE p.id = ?1 and p.entityStatus <> 'DELETED'")
    Optional<Passport> findById(@NonNull Long id);

    @Override
    @NonNull
    @Query("SELECT p FROM Passport p WHERE p.entityStatus = 'ACTIVE' order by p.id asc")
    Page<Passport> findAll(Pageable pageable);

}