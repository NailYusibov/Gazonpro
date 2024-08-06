package com.gitlab.repository;

import com.gitlab.model.Order;
import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @NonNull
    @Query("SELECT r FROM Order r WHERE r.entityStatus = 'ACTIVE' order by r.id asc")
    Page<Order> findAll(Pageable pageable);

    @NonNull
    @Query("SELECT r FROM Order r WHERE r.entityStatus = 'ACTIVE' AND r.user.username = :username order by r.id asc")
    Page<Order> findAll(Pageable pageable, @Param("username") String username);

    @Override
    @NonNull
    @Query("SELECT r FROM Order r WHERE r.entityStatus = 'ACTIVE' order by r.id asc")
    List<Order> findAll();

    @NonNull
    @Query("SELECT r FROM Order r WHERE r.entityStatus = 'ACTIVE' AND r.user.username = :username order by r.id asc")
    List<Order> findAll(@Param("username") String username);

    @NonNull
    @Query("SELECT r FROM Order r WHERE r.entityStatus = 'ACTIVE' AND r.id = :id AND r.user.username = :username order by r.id asc")
    Optional<Order> findById(@Param("id") Long id, @Param("username") String username);

    @NonNull
    @Query("SELECT r FROM Order r JOIN FETCH r.selectedProducts where r.orderStatus = 'NOT_PAID' order by r.id asc")
    List<Order> findOrdersWithNotPaidStatus();
}
