package com.gitlab.repository;

import com.gitlab.model.BankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    @Override
    @Query("SELECT u FROM BankCard u order by u.id asc")
    List<BankCard> findAll();

    @Override
    @Query("SELECT u FROM BankCard u order by u.id asc")
    Page<BankCard> findAll(Pageable pageable);
}