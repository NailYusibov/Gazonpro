package com.gitlab.repository;

import com.gitlab.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("SELECT r FROM ProductImage r WHERE r.someProduct.id = :id and r.someProduct.entityStatus = 'ACTIVE'")
    List<ProductImage> findAllBySomeProductId(Long id);
}
