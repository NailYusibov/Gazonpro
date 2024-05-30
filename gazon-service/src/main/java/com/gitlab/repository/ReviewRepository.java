package com.gitlab.repository;

import com.gitlab.enums.EntityStatus;
import com.gitlab.model.Review;
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
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Override
    @NonNull
    @EntityGraph(value = "Review.reviewImages")
    Optional<Review> findById(@NonNull Long id);


    @NonNull
    @EntityGraph(value = "Review.reviewImages")
    List<Review> findAllByProductId(@NonNull Long id);

    @Override
    @NonNull
    @EntityGraph(value = "Review.reviewImages")
    @Query("SELECT r FROM Review r WHERE r.entityStatus = 'ACTIVE'")
    List<Review> findAll();

    @Override
    @NonNull
    @Query("SELECT r FROM Review r WHERE r.entityStatus = 'ACTIVE'")
    Page<Review> findAll(@NonNull Pageable pageable);

    Long countReviewByProduct_IdAndEntityStatus(Long id, EntityStatus entityStatus);
}